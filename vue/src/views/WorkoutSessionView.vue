<template>
    <div class="min-h-screen text-black p-6 flex flex-col items-center">
        <!-- User Info Display -->
        <div v-if="currentUser" class="mb-4 text-center">
            <p class="text-sm text-gray-600">Logged in as: <span class="font-medium">{{ currentUser.username }}</span></p>
        </div>
        <div v-else class="mb-4 text-center">
            <p class="text-sm text-red-600">Please log in to save your workouts</p>
        </div>

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
                :disabled="activities.length === 0 || isSaving || !currentUser"
                class="w-full px-4 py-3 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
            >
                <span v-if="isSaving" class="mr-2">
                    <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                    </svg>
                </span>
                <span v-if="!currentUser">Login Required</span>
                <span v-else>{{ isSaving ? 'Saving...' : 'Save Workout' }}</span>
            </button>

            <!-- Success/Error Messages -->
            <div v-if="saveMessage" :class="{
                'bg-green-100 border-green-400 text-green-700': saveStatus === 'success',
                'bg-red-100 border-red-400 text-red-700': saveStatus === 'error'
            }" class="border px-4 py-3 rounded">
                {{ saveMessage }}
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
import { API_URL } from '../const';

interface Activity {
    exercise: string;
    reps: number;
    weight: number;
    execution: string;
    deathStop: boolean;
}

interface ExercisePayload {
    name: string;
    reps: number;
    weight: number;
    execution: string;
    deathStop: boolean;
}

interface WorkoutPayload {
    name: string;
    duration: string;
    description: string;
    exercises: ExercisePayload[];
}

interface User {
    id: number;
    username: string;
    email: string;
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

        // Auth and user state
        const currentUser = ref<User | null>(null);

        // Backend integration states
        const isSaving = ref(false);
        const saveMessage = ref('');
        const saveStatus = ref<'success' | 'error' | null>(null);

        const customExercises = ref<string[]>([]);
        const workoutName = ref('Workout Session');

        // Helper function to get auth token
        const getAuthToken = (): string | null => {
            try {
                return localStorage.getItem('authToken');
            } catch (error) {
                console.warn('Could not access localStorage for token');
                return null;
            }
        };

        // Helper function to get current user
        const getCurrentUser = (): User | null => {
            try {
                const userStr = localStorage.getItem('currentUser');
                return userStr ? JSON.parse(userStr) : null;
            } catch (error) {
                console.warn('Could not access localStorage for user data');
                return null;
            }
        };

        // Create axios instance with configuration
        const createApiInstance = (): AxiosInstance => {
            const instance = axios.create({
                baseURL: API_URL,
                timeout: 10000,
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            // Request interceptor for adding auth token
            instance.interceptors.request.use(
                (config) => {
                    const token = getAuthToken();
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
                        console.error('Unauthorized access - token may be expired');
                        // Clear stored auth data
                        try {
                            localStorage.removeItem('authToken');
                            localStorage.removeItem('currentUser');
                        } catch (e) {
                            console.warn('Could not clear localStorage');
                        }
                        currentUser.value = null;
                    } else if (error.response?.status >= 500) {
                        console.error('Server error:', error.response.data);
                    }
                    return Promise.reject(error);
                }
            );

            return instance;
        };

        const apiClient = createApiInstance();

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
            
            // Load current user from localStorage
            currentUser.value = getCurrentUser();
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
            try {
                localStorage.setItem('workoutName', workoutName.value);
            } catch (error) {
                console.warn('Could not save workout name to localStorage');
            }
        };

        // Create workout description from activities
        function createWorkoutDescription(): string {
            const exerciseSummary = activities.value
                .map(activity => `${activity.exercise}: ${activity.reps} reps @ ${activity.weight}kg (${activity.execution})${activity.deathStop ? ' - Death Stop' : ''}`)
                .join(', ');
            
            return `Workout completed in ${elapsedTime.value} seconds. Exercises: ${exerciseSummary}`;
        }

        // Save workout session as single payload with exercises array
        async function saveWorkoutSession() {
            if (activities.value.length === 0 || !currentUser.value) return;

            isSaving.value = true;
            saveMessage.value = '';
            saveStatus.value = null;

            try {
                // Transform activities to exercise payload format
                const exercises: ExercisePayload[] = activities.value.map(activity => ({
                    name: activity.exercise,
                    reps: activity.reps,
                    weight: activity.weight,
                    execution: activity.execution,
                    deathStop: activity.deathStop
                }));

                // Create single workout payload
                const workoutPayload: WorkoutPayload = {
                    name: workoutName.value,
                    duration: elapsedTime.value.toString(),
                    description: createWorkoutDescription(),
                    exercises: exercises
                };

                // Log the payload for debugging
                console.log('Sending workout payload:', JSON.stringify(workoutPayload, null, 2));

                // Send single request with all workout data
                const response = await apiClient.post('/activities', workoutPayload);
                
                saveStatus.value = 'success';
                saveMessage.value = `Successfully saved workout "${workoutName.value}" with ${activities.value.length} exercises!`;
                
                // Clear current workout
                activities.value = [];
                elapsedTime.value = 0;
                startTime.value = Date.now();
                workoutName.value = 'Workout Session';
                
                // Clear success message after 3 seconds
                setTimeout(() => {
                    saveMessage.value = '';
                    saveStatus.value = null;
                }, 3000);

            } catch (error) {
                console.error('Error saving workout:', error);
                
                // Enhanced error logging
                if (axios.isAxiosError(error) && error.response) {
                    console.error('Response status:', error.response.status);
                    console.error('Response data:', error.response.data);
                    console.error('Response headers:', error.response.headers);
                }
                
                let errorMessage = 'Failed to save workout. Please try again.';
                if (axios.isAxiosError(error)) {
                    if (error.response?.status === 401) {
                        errorMessage = 'Your session has expired. Please log in again.';
                    } else if (error.response?.status === 400) {
                        // More specific 400 error handling
                        const responseData = error.response.data;
                        if (typeof responseData === 'string') {
                            errorMessage = `Bad request: ${responseData}`;
                        } else if (responseData?.message) {
                            errorMessage = `Bad request: ${responseData.message}`;
                        } else if (responseData?.errors) {
                            errorMessage = `Validation errors: ${JSON.stringify(responseData.errors)}`;
                        } else {
                            errorMessage = 'Bad request: Please check your input data.';
                        }
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
            // Auth
            currentUser,
            // Backend integration
            saveWorkoutSession,
            isSaving,
            saveMessage,
            saveStatus
        };
    }
});
</script>