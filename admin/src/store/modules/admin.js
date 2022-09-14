export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
}

const state = {
    adminInfo: JSON.parse(window.localStorage.getItem('adminInfo')),

}
const mutations = {
    setAdminInfo(state, data) {
        state.adminInfo = data
        window.localStorage.setItem('adminInfo', data)
    },
}
const actions = {}
const getters = {}
