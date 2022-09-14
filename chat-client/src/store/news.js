import {SET_UNREAD_NEWS_TYPE_MAP} from './constants'
const state = {
    unreadNews: {},
    lastNews: {}, // 好友之间最后发送的一条消息 {roomId1: {}, roomId2: {}}
}
const actions = {
    SET_UNREAD_NEWS({commit}, data) {
        commit('setUnreadNews', data)
    },
    SET_LAST_NEWS({commit}, data) {
        commit('setLastNews', data)
    },
}
const mutations = {
    setUnreadNews(state, data) {
        const {roomId, count, type} = data
    // console.log("设置未读消息：", data, "其类型为：", type, "数量为：", count)
        if (type === SET_UNREAD_NEWS_TYPE_MAP.add) {
            const oldCount = state.unreadNews[roomId] === undefined ? 0 : state.unreadNews[roomId]
            const item = {[roomId]: oldCount + 1}
            state.unreadNews = Object.assign({}, state.unreadNews, item)
        }else if (type === SET_UNREAD_NEWS_TYPE_MAP.clear) { //清除未读消息
            const item = {[roomId]: 0}
            state.unreadNews = Object.assign({}, state.unreadNews, item)
        } else { //其余情况，默认 roomId -> count
        const item = {[roomId]: count}
        state.unreadNews = Object.assign({}, state.unreadNews, item)
        }
    },
    setLastNews(state, data) {
        if (data.type === 'init') {
            state.lastNews = data.res
        } else if (data.type === 'concat') {
            state.lastNews = Object.assign({},state.lastNews,data.res)
        } else if (data.type === 'edit') {
            const {roomId,news} = data.res
            const item = {[roomId]: news}
            state.lastNews = Object.assign({},state.lastNews,item)
        } else if (data.type === 'default') { // 判断是否为新添加的会话
            const {roomId} = data
            if (roomId in state.lastNews) return
            const item = {[roomId]: {time: Date.now(), messageType: 'text', message: ''}}
            state.lastNews = Object.assign({}, state.lastNews, item)
        }
    },
}
const getters = {}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
  }