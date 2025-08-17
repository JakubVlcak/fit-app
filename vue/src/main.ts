import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import GoogleSignInPlugin from 'vue3-google-signin'


const app = createApp(App)
const backendUrl = import.meta.env.VITE_BACKEND_URL
app.use(createPinia())
app.use(router)

app.mount('#app')
