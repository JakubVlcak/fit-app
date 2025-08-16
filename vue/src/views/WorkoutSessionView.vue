<template>
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
                        {{ a.exercise }} - {{ a.reps }} reps - {{ a.weight }}kg ({{ a.execution }})
                    </div>
                    <button @click="removeActivity(i)" class="px-2 py-1 bg-red-500 hover:bg-red-600 rounded text-white">
                        ✕
                    </button>
                </li>
            </ul>
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted, computed } from 'vue';
import vSelect from 'vue-select';
import 'vue-select/dist/vue-select.css';
import { EXERCISES } from '@/data/exercises';

interface Activity {
    exercise: string;
    reps: number;
    weight: number;
    execution: string;
}

export default defineComponent({
    components: {
        'v-select': vSelect
    },
    setup() {
        const elapsedTime = ref(0);
        let intervalId: number;

        const exercise = ref('');
        const reps = ref(1);
        const weight = ref(0);
        const execution = ref('bilateral');
        const activities = ref<Activity[]>([]);
        const searchQuery = ref('');

        const customExercises = ref<string[]>([]);

        const filteredExercises = computed(() => {
            if (!searchQuery.value) return [...EXERCISES, ...customExercises.value];

            const query = searchQuery.value.toLowerCase();
            return [...EXERCISES, ...customExercises.value]
                .filter(ex => ex.toLowerCase().includes(query));
        });

        onMounted(() => {
            intervalId = setInterval(() => {
                elapsedTime.value++;
            }, 1000) as unknown as number;
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
                execution: execution.value
            });
        }

        function removeActivity(index: number) {
            activities.value.splice(index, 1);
        }

        function onSearch(query: string) {
            searchQuery.value = query;
        }

        // Add workoutName ref
        const workoutName = ref(localStorage.getItem('workoutName') || 'Workout Session');

        // Add method to save the name
        const saveWorkoutName = () => {
            localStorage.setItem('workoutName', workoutName.value);
        };


        return {
            workoutName,
            saveWorkoutName,
            elapsedTime,
            exercise,
            reps,
            weight,
            execution,
            activities,
            filteredExercises,
            addActivity,
            removeActivity,
            onSearch,
        };
    }
});
</script>