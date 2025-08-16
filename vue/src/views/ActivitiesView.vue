<template>

    <div class="min-h-screen flex flex-col items-center justify-centet">
        <h1 class="text-center text-black text-2xl mb-6">
            These are activities of {{ username }}
        </h1>

        <div @click="startWorkout"
            class="activities-container bg-gray-600 w-96 h-64 rounded-xl shadow-lg p-6 hover:cursor-pointer transition-colors duration-300">
            <p class="text-center text-white font-bold hover:text-green-300 transition-colors duration-300">
                Start your activity
            </p>
            <div class="text-white text-9xl font-bold text-center hover:text-green-400 transition-colors duration-300">
                +
            </div>
        </div>

    </div>

</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';
export default defineComponent({
    name: 'UserActivities',
    data() {
        return {
            username: '' as string
        };
    },
    mounted() {
        const currentUserJson = localStorage.getItem('currentUser');

        if (currentUserJson) {
            try {
                const currentUser = JSON.parse(currentUserJson);
                this.username = currentUser.username || '';
            } catch (error) {
                console.error('Error parsing currentUser:', error);
                this.username = '';
            }
        }
    },
    setup() {
        const router = useRouter();

        function startWorkout() {
            router.push('/workout');
        }

        return { startWorkout };
    }
});
</script>
