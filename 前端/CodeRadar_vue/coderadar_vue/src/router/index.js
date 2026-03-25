import { createRouter, createWebHistory } from 'vue-router'
import IndexPage from '../views/IndexPage.vue'
import ReviewPage from '../views/ReviewPage.vue'

const routes = [
    {
        path: '/',
        name: 'Index',
        component: IndexPage
    },
    {
        path: '/review',
        name: 'Review',
        component: ReviewPage,
        props: (route) => ({ resultId: route.query.resultId })
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router