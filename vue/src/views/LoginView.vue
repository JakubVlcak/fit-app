<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-lg shadow-md">
            <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
                Login
            </h2>

            <form @submit.prevent="handleLogin" class="mt-8 space-y-6">
                <div class="rounded-md shadow-sm space-y-4">
                    <div>
                        <label for="login-username" class="block text-sm font-medium text-gray-700 mb-1">
                            Username:
                        </label>
                        <input id="login-username" v-model="form.username" type="text" required
                            class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                            placeholder="Enter your username" />
                    </div>

                    <div>
                        <label for="login-password" class="block text-sm font-medium text-gray-700 mb-1">
                            Password:
                        </label>
                        <input id="login-password" v-model="form.password" type="password" required
                            class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                            placeholder="Enter your password" />
                    </div>
                </div>

                <div>
                    <button type="submit"
                        class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                        Login
                    </button>
                </div>

                <div v-if="error" class="text-red-600 text-sm text-center mt-2">
                    {{ error }}
                </div>
            </form>
        </div>
    </div>
</template>

<script>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

export default {
    name: 'LoginComponent',
    setup() {
        const form = reactive({
            username: '',
            password: ''
        });

        const error = ref('');
        const router = useRouter();

        async function handleLogin() {
            try {
                const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        username: form.username,
                        password: form.password
                    })
                });

                if (!response.ok) {
                    error.value = 'Invalid username or password';
                    return;
                }

                const data = await response.json();

                // Store token in localStorage
                localStorage.setItem('authToken', data.token);
                localStorage.setItem('currentUser', JSON.stringify({
                    id: data.id,
                    username: data.username
                }));

                // Redirect to dashboard
                window.location.href = '/dashboard';
            } catch (err) {
                error.value = 'Login failed. Please try again.';
            }
        }

        return {
            form,
            error,
            handleLogin
        };
    }
};
</script>
