import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import RouterView from '@/views/RouterView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ActivitiesView from '@/views/ActivitiesView.vue'
import WorkoutSessionView from '@/views/WorkoutSessionView.vue'
import ActivitiesListView from '@/views/ActivitiesListView.vue'
import GoogleSignInView from '@/views/GoogleSignInView.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/routerview',
      name: 'routerview',
      component: RouterView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/activities',
      name: 'activities',
      component: ActivitiesView,
    },
    {
      path: '/workout',
      name: 'workout',
      component: WorkoutSessionView,
    },
    {
      path: '/listactivities',
      name: 'listactivities',
      component: ActivitiesListView,
    },
    {
      path: '/',
      name: 'google-sign-in',
      component: GoogleSignInView,
    },
  ],
})

export default router
