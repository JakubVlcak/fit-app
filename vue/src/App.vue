<template>
  <div v-if="username">
    Logged in as: <strong>{{ username }}</strong><br>
    <button @click="logout">Logout</button>
  </div>
  <RouterLink to="/routerview">RouterView</RouterLink><br>
  <RouterView />
</template>



<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { useRouter, RouterLink, RouterView } from 'vue-router';

export default defineComponent({
  name: 'App',
  components: {
    RouterLink,
    RouterView
  },
  setup() {
    const username = ref<string>('');
    const router = useRouter();

    onMounted(() => {
      const currentUserJson = localStorage.getItem('currentUser');

      if (currentUserJson) {
        try {
          const currentUser = JSON.parse(currentUserJson);
          username.value = currentUser.username || '';
        } catch (error) {
          console.error('Error parsing currentUser:', error);
          username.value = '';
        }
      }
    });

    function logout() {
      localStorage.removeItem('authToken');
      localStorage.removeItem('currentUser');
      window.location.href = '/login'; // Forces full page reload
    }

    return {
      username,
      logout
    };
  }
});
</script>