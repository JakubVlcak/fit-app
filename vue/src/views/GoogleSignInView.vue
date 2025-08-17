<template>
  <div class="google-signin-container">
    <h2>Sign In with Google</h2>
    
    <!-- Google Sign-In button will be rendered here -->
    <div id="google-signin-button" class="google-button-container"></div>
    
    <div v-if="isLoading" class="loading">
      Authenticating...
    </div>
    
    <div v-if="errorMessage" class="error">
      {{ errorMessage }}
    </div>
    
    <div v-if="user" class="user-info">
      <h3>Welcome, {{ user.name }}!</h3>
      <img :src="user.picture" :alt="user.name" class="profile-picture">
      <p>{{ user.email }}</p>
      <button @click="signOut" class="sign-out-btn">Sign Out</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'

export default {
  name: 'GoogleSignInDirect',
  setup() {
    const user = ref(null)
    const isLoading = ref(false)
    const errorMessage = ref('')
    const isGoogleLoaded = ref(false)

    const initializeGoogleSignIn = () => {
      if (window.google && window.google.accounts) {
        window.google.accounts.id.initialize({
          client_id: '205962209064-jg2cajmcnqna51eh763pq9ao2m3it4h4.apps.googleusercontent.com',
          callback: handleCredentialResponse
        })

        // Render the sign-in button
        window.google.accounts.id.renderButton(
          document.getElementById('google-signin-button'),
          {
            theme: 'outline',
            size: 'large',
            width: '300',
            text: 'signin_with',
            shape: 'rectangular'
          }
        )

        // Optional: Show One Tap prompt
        // window.google.accounts.id.prompt()
        
        isGoogleLoaded.value = true
      } else {
        console.error('Google Identity Services not loaded')
      }
    }

    const handleCredentialResponse = async (response) => {
      console.log('Google credential response:', response)
      isLoading.value = true
      errorMessage.value = ''

      try {
        // Send the JWT token to your backend
        const backendResponse = await fetch('http://localhost:8080'+'/test', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${response.credential}`
          },
        })

        if (!backendResponse.ok) {
          throw new Error('Backend authentication failed')
        }

        const userData = await backendResponse.json()
        console.log('Backend response:', userData)
        
        // Store user data and token
        user.value = userData.user
        if (userData.token) {
          localStorage.setItem('authToken', userData.token)
        }

        // Emit authentication success event or handle routing
        // this.$emit('auth-success', userData)
        // or this.$router.push('/dashboard')

      } catch (error) {
        console.error('Authentication failed:', error)
        errorMessage.value = 'Authentication failed. Please try again.'
      } finally {
        isLoading.value = false
      }
    }

    const signOut = async () => {
      try {
        // Sign out from Google
        if (window.google && window.google.accounts) {
          window.google.accounts.id.disableAutoSelect()
        }
        
        // Clear stored data
        localStorage.removeItem('authToken')
        
        // Optionally call backend logout
        const token = localStorage.getItem('authToken')
        if (token) {
          await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          })
        }
        
        user.value = null
        
        // Re-render the sign-in button
        if (isGoogleLoaded.value) {
          setTimeout(() => {
            initializeGoogleSignIn()
          }, 100)
        }
        
      } catch (error) {
        console.error('Sign out error:', error)
      }
    }

    const loadGoogleScript = () => {
      return new Promise((resolve, reject) => {
        if (window.google && window.google.accounts) {
          resolve()
          return
        }

        const script = document.createElement('script')
        script.src = 'https://accounts.google.com/gsi/client'
        script.async = true
        script.defer = true
        script.onload = resolve
        script.onerror = reject
        document.head.appendChild(script)
      })
    }

    onMounted(async () => {
      try {
        await loadGoogleScript()
        initializeGoogleSignIn()
      } catch (error) {
        console.error('Failed to load Google Sign-In:', error)
        errorMessage.value = 'Failed to load Google Sign-In'
      }
    })

    onUnmounted(() => {
      // Clean up Google Sign-In
      if (window.google && window.google.accounts) {
        window.google.accounts.id.cancel()
      }
    })

    return {
      user,
      isLoading,
      errorMessage,
      signOut
    }
  }
}
</script>

<style scoped>
.google-signin-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
  font-family: Arial, sans-serif;
}

.google-button-container {
  margin: 2rem 0;
  display: flex;
  justify-content: center;
}

.loading {
  margin: 1rem 0;
  color: #666;
  font-style: italic;
}

.error {
  margin: 1rem 0;
  padding: 1rem;
  background-color: #fee;
  border: 1px solid #fcc;
  border-radius: 4px;
  color: #c66;
}

.user-info {
  margin-top: 2rem;
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.profile-picture {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 1rem 0;
}

.sign-out-btn {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.sign-out-btn:hover {
  background-color: #c82333;
}
</style>