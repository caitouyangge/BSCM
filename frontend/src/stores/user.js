import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  
  const setUserInfo = (info) => {
    userInfo.value = info
  }
  
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const logout = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('token')
  }
  
  return {
    userInfo,
    token,
    setUserInfo,
    setToken,
    logout
  }
})

