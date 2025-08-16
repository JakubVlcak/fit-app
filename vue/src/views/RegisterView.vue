<template>
    <div class="auth-container">
        <h2 class="text-center mb-4">Register</h2>

        <form @submit.prevent="handleSubmit" class="auth-form">
            <div class="form-group">
                <label for="username">Username:</label>
                <input id="username" v-model="form.username" type="text" required class="form-input"
                    :class="{ 'input-error': errors.username }" @input="clearError('username')" />
                <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input id="password" v-model="form.password" type="password" required class="form-input"
                    :class="{ 'input-error': errors.password }" @input="clearError('password')" />
                <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
            </div>

            <button type="submit" class="submit-btn" :disabled="isSubmitting">
                {{ isSubmitting ? 'Registering...' : 'Register' }}
            </button>

            <div v-if="successMessage" class="success-message">
                {{ successMessage }}
            </div>
        </form>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const form = reactive({
    username: '',
    password: ''
})

const errors = reactive({
    username: '',
    password: '',
    general: ''
})

const isSubmitting = ref(false)
const successMessage = ref('')

function clearError(field) {
    errors[field] = ''
    errors.general = ''
}

async function handleSubmit() {
    // Clear previous errors
    errors.username = ''
    errors.password = ''
    errors.general = ''
    successMessage.value = ''

    // Basic validation
    if (!form.username.trim()) {
        errors.username = 'Username is required'
        return
    }
    if (!form.password) {
        errors.password = 'Password is required'
        return
    }

    isSubmitting.value = true
    try {
        const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: form.username,
                password: form.password
            })
        })

        const data = await response.text() // Changed to text() since backend returns String

        if (!response.ok) {
            // Handle different error cases
            if (response.status === 400) {
                errors.general = data || 'Username is already taken'
            } else {
                errors.general = 'Registration failed. Please try again.'
            }
            return
        }

        // Success case
        successMessage.value = data || 'Registration successful!'
        form.username = ''
        form.password = ''

    } catch (error) {
        errors.general = 'Network error. Please check your connection.'
    } finally {
        isSubmitting.value = false
    }
}
</script>

<style scoped>
.auth-container {
    max-width: 400px;
    margin: 2rem auto;
    padding: 2rem;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.auth-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.form-input {
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

.form-input:focus {
    outline: none;
    border-color: #646cff;
}

.input-error {
    border-color: #ff4444;
}

.error-message {
    color: #ff4444;
    font-size: 0.875rem;
}

.success-message {
    color: #00c851;
    text-align: center;
    margin-top: 1rem;
}

.submit-btn {
    padding: 0.75rem;
    background-color: #646cff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1rem;
    transition: background-color 0.2s;
}

.submit-btn:hover {
    background-color: #535bf2;
}

.submit-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.text-center {
    text-align: center;
}

.mb-4 {
    margin-bottom: 1rem;
}
</style>