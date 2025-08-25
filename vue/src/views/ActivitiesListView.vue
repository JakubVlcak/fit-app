<template>
    <div class="min-h-screen text-black p-6 flex flex-col items-center">
        <!-- User Info Display -->
        <div v-if="currentUser" class="mb-6 text-center">
            <h1 class="text-3xl font-bold mb-2">Your Activities</h1>
            <p class="text-sm text-gray-600">Logged in as: <span class="font-medium">{{ currentUser.username }}</span></p>
        </div>
        <div v-else class="mb-6 text-center">
            <h1 class="text-3xl font-bold mb-2">Activities</h1>
            <p class="text-sm text-red-600">Please log in to view your activities</p>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="flex items-center justify-center py-8">
            <svg class="animate-spin h-8 w-8 text-blue-500" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
            </svg>
            <span class="ml-2 text-gray-600">Loading activities...</span>
        </div>

        <!-- Error State -->
        <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6 max-w-2xl">
            <p class="font-medium">Error loading activities:</p>
            <p>{{ error }}</p>
            <button @click="fetchActivities" class="mt-2 px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-sm">
                Retry
            </button>
        </div>

        <!-- Activities List -->
        <div v-if="!isLoading && !error" class="w-full max-w-4xl">
            <!-- Summary Stats -->
            <div v-if="activities.length > 0" class="bg-gray-100 p-4 rounded-lg mb-6">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-center">
                    <div>
                        <p class="text-2xl font-bold text-blue-600">{{ activities.length }}</p>
                        <p class="text-sm text-gray-600">Total Workouts</p>
                    </div>
                    <div>
                        <p class="text-2xl font-bold text-green-600">{{ totalExercises }}</p>
                        <p class="text-sm text-gray-600">Total Exercises</p>
                    </div>
                    <div>
                        <p class="text-2xl font-bold text-purple-600">{{ Math.round(averageDuration) }}s</p>
                        <p class="text-sm text-gray-600">Avg Duration</p>
                    </div>
                </div>
            </div>

            <!-- No Activities State -->
            <div v-if="activities.length === 0" class="text-center py-8">
                <div class="text-gray-400 mb-4">
                    <svg class="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                    </svg>
                </div>
                <h3 class="text-lg font-medium text-gray-900 mb-2">No activities yet</h3>
                <p class="text-gray-600 mb-4">Start your fitness journey by creating your first workout!</p>
                <button @click="$router.push('/workout')" class="px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600">
                    Create Workout
                </button>
            </div>

            <!-- Activities Cards -->
            <div v-else class="space-y-4">
                <div v-for="activity in activities" :key="activity.id" 
                     class="bg-white border border-gray-200 rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow">
                    <!-- Activity Header -->
                    <div class="flex justify-between items-start mb-4">
                        <div>
                            <h3 class="text-lg font-semibold text-gray-900">{{ activity.name }}</h3>
                            <p class="text-sm text-gray-600">{{ formatDate(activity.createdAt) }}</p>
                        </div>
                        <div class="text-right">
                            <p class="text-sm font-medium text-blue-600">{{ activity.duration }}s</p>
                            <p class="text-xs text-gray-500">Duration</p>
                        </div>
                    </div>

                    <!-- Activity Description -->
                    <div v-if="activity.description" class="mb-4">
                        <p class="text-sm text-gray-700">{{ activity.description }}</p>
                    </div>

                    <!-- Exercises List -->
                    <div v-if="activity.exercises && activity.exercises.length > 0" class="mb-4">
                        <h4 class="text-md font-medium text-gray-800 mb-2">
                            Exercises ({{ activity.exercises.length }})
                        </h4>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
                            <div v-for="exercise in activity.exercises" :key="exercise.id"
                                 class="bg-gray-50 p-3 rounded border">
                                <div class="flex justify-between items-center">
                                    <div>
                                        <p class="font-medium text-sm">{{ exercise.name }}</p>
                                        <p class="text-xs text-gray-600">
                                            {{ exercise.reps }} reps • {{ exercise.weight }}kg • {{ exercise.execution }}
                                            <span v-if="exercise.deathStop" class="text-red-600 font-medium">• Death Stop</span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Activity Actions -->
                    <div class="flex justify-end space-x-2">
                        <button @click="deleteActivity(activity.id)" 
                                :disabled="isDeletingId === activity.id"
                                class="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 disabled:opacity-50 text-sm flex items-center">
                            <span v-if="isDeletingId === activity.id" class="mr-1">
                                <svg class="animate-spin h-3 w-3" viewBox="0 0 24 24">
                                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
                                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                                </svg>
                            </span>
                            {{ isDeletingId === activity.id ? 'Deleting...' : 'Delete' }}
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <div v-if="message" :class="{
            'bg-green-100 border-green-400 text-green-700': messageType === 'success',
            'bg-red-100 border-red-400 text-red-700': messageType === 'error'
        }" class="fixed top-4 right-4 border px-4 py-3 rounded shadow-lg">
            {{ message }}
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios, { AxiosInstance } from 'axios';
import { API_URL } from '../const';

interface Exercise {
    id: number;
    name: string;
    reps: number;
    weight: number;
    execution: string;
    deathStop: boolean;
}

interface Activity {
    id: number;
    name: string;
    duration: string;
    description: string;
    createdAt: string;
    exercises: Exercise[];
}

interface User {
    id: number;
    username: string;
    email: string;
}

export default defineComponent({
    setup() {
        const activities = ref<Activity[]>([]);
        const isLoading = ref(false);
        const error = ref('');
        const currentUser = ref<User | null>(null);
        const isDeletingId = ref<number | null>(null);
        const message = ref('');
        const messageType = ref<'success' | 'error'>('success');

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
                    }
                    return Promise.reject(error);
                }
            );

            return instance;
        };

        const apiClient = createApiInstance();

        // Computed properties for stats
        const totalExercises = computed(() => {
            return activities.value.reduce((total, activity) => {
                return total + (activity.exercises?.length || 0);
            }, 0);
        });

        const averageDuration = computed(() => {
            if (activities.value.length === 0) return 0;
            const totalDuration = activities.value.reduce((total, activity) => {
                return total + parseInt(activity.duration || '0');
            }, 0);
            return totalDuration / activities.value.length;
        });

        // Fetch activities from the API
        async function fetchActivities() {
            if (!currentUser.value) {
                error.value = 'Please log in to view your activities';
                return;
            }

            isLoading.value = true;
            error.value = '';

            try {
                console.log('Fetching activities from:', `${API_URL}/activities`);
                const response = await apiClient.get('/activities');
                
                console.log('Activities response:', response.data);
                activities.value = response.data || [];
                
            } catch (err) {
                console.error('Error fetching activities:', err);
                
                let errorMessage = 'Failed to load activities. Please try again.';
                if (axios.isAxiosError(err)) {
                    if (err.response?.status === 401) {
                        errorMessage = 'Your session has expired. Please log in again.';
                    } else if (err.response?.status === 404) {
                        errorMessage = 'Activities endpoint not found. Please contact support.';
                    } else if (err.response?.status >= 500) {
                        errorMessage = 'Server error. Please try again later.';
                    } else if (err.response?.data?.message) {
                        errorMessage = err.response.data.message;
                    }
                } else if (err instanceof Error) {
                    errorMessage = err.message;
                }
                
                error.value = errorMessage;
            } finally {
                isLoading.value = false;
            }
        }

        // Delete activity
        async function deleteActivity(activityId: number) {
            if (!currentUser.value) return;

            const confirmed = confirm('Are you sure you want to delete this activity? This action cannot be undone.');
            if (!confirmed) return;

            isDeletingId.value = activityId;

            try {
                await apiClient.delete(`/activities/${activityId}`);
                
                // Remove from local array
                activities.value = activities.value.filter(activity => activity.id !== activityId);
                
                showMessage('Activity deleted successfully!', 'success');
                
            } catch (err) {
                console.error('Error deleting activity:', err);
                
                let errorMessage = 'Failed to delete activity. Please try again.';
                if (axios.isAxiosError(err)) {
                    if (err.response?.status === 401) {
                        errorMessage = 'Your session has expired. Please log in again.';
                    } else if (err.response?.status === 404) {
                        errorMessage = 'Activity not found or already deleted.';
                    } else if (err.response?.data?.message) {
                        errorMessage = err.response.data.message;
                    }
                }
                
                showMessage(errorMessage, 'error');
            } finally {
                isDeletingId.value = null;
            }
        }

        // Show temporary message
        function showMessage(msg: string, type: 'success' | 'error') {
            message.value = msg;
            messageType.value = type;
            
            setTimeout(() => {
                message.value = '';
            }, 3000);
        }

        // Format date for display
        function formatDate(dateString: string): string {
            try {
                const date = new Date(dateString);
                return date.toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                });
            } catch (error) {
                return dateString;
            }
        }

        onMounted(() => {
            // Load current user from localStorage
            currentUser.value = getCurrentUser();
            
            // Fetch activities if user is logged in
            if (currentUser.value) {
                fetchActivities();
            }
        });

        return {
            activities,
            isLoading,
            error,
            currentUser,
            isDeletingId,
            message,
            messageType,
            totalExercises,
            averageDuration,
            fetchActivities,
            deleteActivity,
            formatDate
        };
    }
});
</script>