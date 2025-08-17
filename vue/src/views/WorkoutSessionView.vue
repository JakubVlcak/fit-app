// Helper function to get exercise ID - you'll need to implement this based on your exercise table
        function getExerciseId(exerciseName: string): string {
            // This should map exercise names to your exercise table IDs
            // For now, using a simple hash - replace with actual lookup
            const exerciseMap: { [key: string]: string } = {
                // Add your exercise name to ID mappings here
                // 'Push-up': 'exercise_uuid_1',
                // 'Squat': 'exercise_uuid_2',
                // etc.
            };
            
            return exerciseMap[exerciseName] || `exercise_${exerciseName.toLowerCase().replace(/\s+/g, '_')}`;
        }<template>
    <div class="min-h-screen text-black p-6 flex flex-col items-center">
        <div class="mb-4">
            <input v-model="workoutName"
                class="text-2xl font-bold text-center bg-transparent border-b border-transparent hover:border-gray-300 focus:border-yellow-500 focus:outline-none"
                placeholder="Workout Session" @blur="saveWorkoutName">
        </div>

        <div class="mb-6">
            <p class="text-lg font-semibold">⏱ Time: {{ elapsedTime }}s</p>
        </div>

        <div class="space-y-4 max-w-md w-full">
            <div>
                <label class="block mb-1 font-medium">Exercise</label>
                <v-select v-model="exercise" :options="filteredExercises" placeholder="Search or select an exercise"
                    class="text-black" :filterable="true" @search="onSearch">
                    <template #no-options="{ search }">
                        <div v-if="search" class="p-2 text-gray-500">
                            No exercises found for "{{ search }}".
                        </div>
                        <div v-else class="p-2 text-gray-500">
                            Start typing to search exercises
                        </div>
                    </template>
                </v-select>
            </div>

            <div>
                <label class="block mb-1 font-medium">Reps</label>
                <input type="number" v-model.number="reps" min="1"
                    class="w-full p-2 rounded border border-gray-300 text-black" />
            </div>

            <div>
                <label class="block mb-1 font-medium">Weight (kg)</label>
                <input type="number" v-model.number="weight" min="0"
                    class="w-full p-2 rounded border border-gray-300 text-black" />
            </div>

            <div>
                <label class="block mb-1 font-medium">Execution</label>
                <select v-model="execution" class="w-full p-2 rounded border border-gray-300 text-black">
                    <option value="bilateral">Bilateral</option>
                    <option value="unilateral">Unilateral</option>
                </select>
            </div>

            <div>
                <label class="flex items-center mb-1 font-medium">
                    <input type="checkbox" v-model="deathStop" class="mr-2" />
                    Death Stop
                </label>
            </div>

            <button @click="addActivity" :disabled="!exercise"
                class="mt-4 px-4 py-2 bg-yellow-500 rounded hover:bg-yellow-400 disabled:opacity-50 disabled:cursor-not-allowed">
                Add Set
            </button>
        </div>

        <div class="mt-8 w-full max-w-md">
            <h2 class="text-xl font-bold mb-2">Exercises</h2>
            <ul class="space-y-2">
                <li v-for="(a, i) in activities" :key="i"
                    class="bg-gray-100 p-3 rounded flex justify-between items-center">
                    <div>
                        {{ a.exercise }} - {{ a.reps }} reps - {{ a.weight }}kg ({{ a.execution }}) - Death Stop: {{ a.deathStop ? 'Yes' : 'No' }}
                    </div>
                    <button @click="removeActivity(i)" class="px-2 py-1 bg-red-500 hover:bg-red-600 rounded text-white">
                        ✕
                    </button>
                </li>
            </ul>
        </div>

        <!-- Save/Send Workout Section -->
        <div class="mt-8 w-full max-w-md space-y-4">
            <button 
                @click="saveWorkoutSession" 
                :disabled="activities.length === 0 || isSaving"
                class="w-full px-4 py-3 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
            >
                <span v-if="isSaving" class="mr-2">
                    <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                    </svg>
                </span>
                {{ isSaving ? 'Saving...' : 'Save Activities' }}
            </button>

            <!-- Success/Error Messages -->
            <div v-if="saveMessage" :class="{
                'bg-green-100 border-green-400 text-green-700': saveStatus === 'success',
                'bg-red-100 border-red-400 text-red-700': saveStatus === 'error'
            }" class="border px-4 py-3 rounded">
                {{ saveMessage }}
            </div>

            <!-- Activity History -->
            <div v-if="savedActivities.length > 0" class="mt-6">
                <h3 class="text-lg font-semibold mb-2">Recent Activities</h3>
                <div class="space-y-2 max-h-40 overflow-y-auto">
                    <div v-for="activity in savedActivities" :key="activity.activity_id" 
                         class="bg-gray-50 p-2 rounded text-sm">
                        <div class="font-medium">{{ activity.name }}</div>
                        <div class="text-gray-600">
                            {{ new Date(activity.date).toLocaleDateString() }} - 
                            {{ activity.exercise_name }} - 
                            {{ activity.reps }} reps @ {{ activity.weight }}kg
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted, computed } from 'vue';
import vSelect from 'vue-select';
import 'vue-select/dist/vue-select.css';
import axios, { AxiosInstance } from 'axios';
import { EXERCISES } from '../data/exercises';

interface Activity {
    exercise: string;
    reps: number;
    weight: number;
    execution: string;
    deathStop: boolean;
}

interface ActivityPayload {
    name: string;
    date: string;
    duration: string; // ISO duration format or timestamp
    description?: string;
    user_id: string; // You'll need to get this from authentication
    exercise_id: string; // This should match your exercise table
    reps: number;
    weight: number;
    execution: string;
    death_stop: boolean;
}

interface SavedActivity {
    activity_id: string;
    name: string;
    date: string;
    duration: string;
    exercise_name: string;
    reps: number;
    weight: number;
}

export default defineComponent({
    components: {
        'v-select': vSelect
    },
    setup() {
        const elapsedTime = ref(0);
        const startTime = ref(Date.now());
        let intervalId: number;

        const exercise = ref('');
        const reps = ref(1);
        const weight = ref(0);
        const execution = ref('bilateral');
        const activities = ref<Activity[]>([]);
        const deathStop = ref(false);
        const searchQuery = ref('');

        // Backend integration states
        const isSaving = ref(false);
        const saveMessage = ref('');
        const saveStatus = ref<'success' | 'error' | null>(null);
        const savedActivities = ref<SavedActivity[]>([]);

        const customExercises = ref<string[]>([]);
        const workoutName = ref('Workout Session');

        // Create axios instance with configuration
        const createApiInstance = (): AxiosInstance => {
            const instance = axios.create({
                baseURL: 'https://your-api.com/api', // Replace with your actual API URL
                timeout: 10000, // 10 second timeout
                headers: {
                    'Content-Type': 'application/json',
                    // Add default headers here
                }
            });

            // Request interceptor for adding auth token
            instance.interceptors.request.use(
                (config) => {
                    // Add auth token if available
                    const token = getAuthToken(); // Implement this function
                    if (token) {
                        config.headers.Authorization = `Bearer ${token}`;
                    }
                    return config;
                },
                (error) => {
                    return Promise.reject(error);
                }
            );

            // Response interceptor for handling common errors
            instance.interceptors.response.use(
                (response) => response,
                (error) => {
                    if (error.response?.status === 401) {
                        // Handle unauthorized - redirect to login
                        console.error('Unauthorized access - please login');
                        // handleLogout(); // Implement this function
                    } else if (error.response?.status >= 500) {
                        console.error('Server error:', error.response.data);
                    }
                    return Promise.reject(error);
                }
            );

            return instance;
        };

        const apiClient = createApiInstance();

        // Helper function to get auth token - implement based on your auth system
        const getAuthToken = (): string | null => {
            // Replace with your actual token retrieval logic
            // return localStorage.getItem('authToken');
            // or return store.state.auth.token;
            return null; // Placeholder
        };

        const filteredExercises = computed(() => {
            if (!searchQuery.value) return [...EXERCISES, ...customExercises.value];

            const query = searchQuery.value.toLowerCase();
            return [...EXERCISES, ...customExercises.value]
                .filter(ex => ex.toLowerCase().includes(query));
        });

        onMounted(() => {
            startTime.value = Date.now();
            intervalId = setInterval(() => {
                elapsedTime.value++;
            }, 1000) as unknown as number;
            
            // Load recent activities when component mounts
            loadRecentActivities();
        });

        onUnmounted(() => {
            clearInterval(intervalId);
        });

        function addActivity() {
            if (!exercise.value) return;

            activities.value.push({
                exercise: exercise.value,
                reps: reps.value,
                weight: weight.value,
                execution: execution.value,
                deathStop: deathStop.value
            });
        }

        function removeActivity(index: number) {
            activities.value.splice(index, 1);
        }

        function onSearch(query: string) {
            searchQuery.value = query;
        }

        const saveWorkoutName = () => {
            // Note: Using in-memory storage instead of localStorage for artifacts
            // In a real app, you'd use localStorage.setItem('workoutName', workoutName.value);
        };

        // Save individual activities to backend using axios
        async function saveWorkoutSession() {
            if (activities.value.length === 0) return;

            isSaving.value = true;
            saveMessage.value = '';
            saveStatus.value = null;

            try {
                const sessionDate = new Date().toISOString();
                const sessionDuration = elapsedTime.value.toString();
                const userId = 'user123'; // Replace with actual user ID from authentication
                
                // Save each activity individually using axios
                const savePromises = activities.value.map(async (activity) => {
                    const activityData: ActivityPayload = {
                        name: `${workoutName.value} - ${activity.exercise}`,
                        date: sessionDate,
                        duration: sessionDuration,
                        description: `${activity.reps} reps at ${activity.weight}kg (${activity.execution})${activity.deathStop ? ' - Death Stop' : ''}`,
                        user_id: userId,
                        exercise_id: getExerciseId(activity.exercise),
                        reps: activity.reps,
                        weight: activity.weight,
                        execution: activity.execution,
                        death_stop: activity.deathStop
                    };

                    const response = await apiClient.post('/activities', activityData);
                    return response.data;
                });

                // Wait for all activities to be saved
                const results = await Promise.all(savePromises);
                
                saveStatus.value = 'success';
                saveMessage.value = `Successfully saved ${activities.value.length} activities!`;
                
                // Clear current workout
                activities.value = [];
                elapsedTime.value = 0;
                startTime.value = Date.now();
                workoutName.value = 'Workout Session';
                
                // Reload recent activities
                await loadRecentActivities();
                
                // Clear success message after 3 seconds
                setTimeout(() => {
                    saveMessage.value = '';
                    saveStatus.value = null;
                }, 3000);

            } catch (error) {
                console.error('Error saving activities:', error);
                
                let errorMessage = 'Failed to save activities. Please try again.';
                if (axios.isAxiosError(error)) {
                    if (error.response?.status === 401) {
                        errorMessage = 'Please login to save activities.';
                    } else if (error.response?.status >= 500) {
                        errorMessage = 'Server error. Please try again later.';
                    } else if (error.response?.data?.message) {
                        errorMessage = error.response.data.message;
                    }
                } else if (error instanceof Error) {
                    errorMessage = error.message;
                }
                
                saveStatus.value = 'error';
                saveMessage.value = errorMessage;
                
                // Clear error message after 5 seconds
                setTimeout(() => {
                    saveMessage.value = '';
                    saveStatus.value = null;
                }, 5000);
            } finally {
                isSaving.value = false;
            }
        }

        // Load recent activities from backend using axios
        async function loadRecentActivities() {
            try {
                const response = await apiClient.get('/activities/history', {
                    params: { limit: 10 }
                });
                
                savedActivities.value = response.data;
            } catch (error) {
                console.error('Error loading activity history:', error);
                // Fail silently for history loading, but you could show a toast notification
            }
        }

        return {
            workoutName,
            saveWorkoutName,
            elapsedTime,
            exercise,
            reps,
            weight,
            execution,
            deathStop,
            activities,
            filteredExercises,
            addActivity,
            removeActivity,
            onSearch,
            // Backend integration
            saveWorkoutSession,
            isSaving,
            saveMessage,
            saveStatus,
            savedActivities
        };
    }
});
</script>