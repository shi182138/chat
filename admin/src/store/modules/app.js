const actions = {}
const mutations = {
    selectmenu(state, data) {
        if (data.name != 'home') {
            state.currentTab = data
            const result =  state.tabList.findIndex(item => item.name == data.name)
            if (result == -1) {
                state.tabList.push(data)
            } else {
                state.currentTab = null
            }
        }
    },
    closeTab(state,targetName) {
        state.tabList = state.tabList.filter(tab => tab.name !== targetName);
    },
    setAllUser(state, data) {
        state.allUser = data
    },
    setAllGroup(state, data) {
        state.allGroup = data
    }
}
const state = {
    tabList: [
        {
            path: '/home',
            name: 'home',
            label: '首页',
            icon: 'home'
        }
    ],
    currentTab: null,
    allUser: [],
    allGroup: []
}
const getters = {}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
}
