const actions = {
    SET_USERINFO({commit},data) {
        commit('setUserInfo' , data)
    }
}
const mutations = {
    ShowRecentList(state) {
        state.itemName = 'RecentList'
    },
    FriendGroupList(state) {
        state.itemName = 'FriendGroupList';
        state.itemName1 = ''
    },
    ShowChatWindow(state){
        state.itemName1 = 'ChatWindow'
    },
    setUserInfo(state, data) {
        state.userInfo = data;
        window.localStorage.setItem('userInfo',JSON.stringify(data))
    },
    IsShowFriendInfo(state){
        state.itemName1 = 'FriendInfo'
    },
    IsShowSearchPanel(state){
        state.itemName = 'SearchPanel'
    },
    IsShowGroupInfo(state) {
        state.itemName1 = 'GroupInfo'
    },
    // 要删除
    showNewPanel(state){
        state.itemName1 = 'ValidateFriend'
    },
    userBus(state, data) {
        state.userBus = data 
    },
    SetFriendInfo(state, data) {
        state.friendInfo = data
    },
    initialInterface(state) {
        state.itemName1 = ''
    },
    //展示添加
    setDrawer(state) {
        state.isShowDrawer = !state.isShowDrawer
    },
    closeDrawer(state) {
        state.isShowDrawer = false
    }
}
const state = {
    itemName:"RecentList",
    showChatWindow:false,
    userInfo: JSON.parse(window.localStorage.getItem('userInfo')),
    itemName1:'',
    userBus: [],//公用
    friendInfo: {},
    // showStrangerInfo:false,
    once: true,
    isShowDrawer: false,
}
const getters = {
}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
  }