
const actions = {
    SET_AGREE_FRIEND_VALIDATE({commit}, data) {
        commit('setAgreeFriendValidate', data)
    },
    SET_RECENT_CONVERSATION({commit}, data) {
        commit('setRecentConversation', data)
    },
    SET_CURRENT_CONVERSATION({commit}, data) {
        commit('setCurrentConversation', data)
    },
    SET_ALL_CONVERSATION({commit}, data) {
        commit('setAllConversation', data)
    },
    SET_ALL_FRIENDS({commit}, data) {
        commit('setAllFriends', data)
    },
    SET_ADD_MEMBER_LIST({commit}, data) {
        commit('setAddMemberList', data)
    },
    SET_ALL_GROUPS(content,data) {
        content.commit('setAllGroups',data)
    },
    // SET_SYS_MESSAGE({commit}, data) {
    //     commit('setSysMessage',data)
    // }
    SET_ISTOCOARTBOARD({commit}, data) {
        commit('setIsToCoArtBoard', data)
    },
    SET_IS_AUDIOING({commit}, data) {
        commit('setIsAudioing', data)
    },
    SET_IS_VIDEOING({commit}, data) {
        commit('setIsVideoing', data)
    },
    ClearRight({commit}, data) {
        commit('clearRight')
    }
    
}
const mutations = {
    // setSysMessage(state, data) {
    //     state.sysMessage = data
    // },
    clearRight(state) {
        state.rightList = []
    },
    SetSysUsers(state,data) {
        state.sysUsers = data
        window.localStorage.setItem('sysUsers',JSON.stringify(data))
    },
    // setCurrentConversation(state, data) {
    //     state.currentConversation = data
    // },   -----------------------带解放
    setAgreeFriendValidate(state, data) {
        state.agreeFriendValidate = data
    },
    setRecentConversation(state, data) {
        const res = data.data
        if (data.type === 'init') {
            state.recentConversation = res
        } else if (data.type === 'add') {
            const ids = state.recentConversation.map(item => item.id)
            console.log('id');
            console.log(res.id);
            const newData = !ids.includes(res.id) ? [...state.recentConversation, res] : [...state.recentConversation] //判断是否重复
            state.recentConversation = newData
        } else if (data.type === 'delete') {
            const newData = JSON.parse(JSON.stringify(state.recentConversation))
            let index
            if (res.groupInfo) { //若是群聊，则根据roomId来过滤
                index = state.recentConversation.findIndex(item => item.roomId === res.roomId)
            } else {
                index = state.recentConversation.findIndex(item => item.id === res.id)
            }
            index !== -1 && newData.splice(index,1)
            state.recentConversation = newData
        } else if (data.type === 'edit') {
            state.recentConversation.map(item => {
                if (item.roomId == res.roomId) {
                    item.delFriendStatus = 1
                }
            })
        }
    },
    setCurrentConversation(state, data) {
        if (data.type == 'edit') {
            if (state.currentConversation.roomId == data.data.roomId) {
                state.currentConversation.delFriendStatus = 1
            }
        } else if (data.type == 'modifyBeiZhu') {
            state.currentConversation.beiZhu = data.beiZhu
        } else {
            state.currentConversation = data
        }
    },
    setAllConversation(state, data) {
        state.allConversation = [...state.allConversation, ...data]
        // console.log("所有会话为：", state.allConversation)
    },
    setAllFriends(state, data) {
        const {resource, type} = data
        // console.log("setAllFriends：", resource, "type：", type)
        if (type === 'init') {
          // resourece === [{...}, {...}, ...]
          state.allFriends = resource
        } else if (type === 'delete') { //删除的时候resource带的是好友id
          // resource === 删除被好友ID
          state.allFriends = (state.allFriends || []).filter(item => item.id !== resource)
        }
    },
    setAllGroups(state, data) {
        const {resource, type} = data
        if (type == 'init') {
            state.allGroups = resource
        } else if (type == 'add') {
            state.allGroups = [...state.allGroups, resource]
        } else if (type == 'delete') {
            state.allGroups = (state.allGroups || []).filter(item => item.groupId !== resource)
        }
    },
    setAddMemberList(state, data) {
        if (data.type == 'edit') {
            state.addMemberLis.forEach(memberItem => {
                if (memberItem.id == data.addItem) {
                    memberItem.isAdd = !memberItem.isAdd
                    // console.log(memberItem.isAdd);
                }
            })
            state.rightList = state.addMemberLis.filter(item => item.isAdd == true )
        } else {
            state.addMemberLis = data
        }
    },
    setIsToCoArtBoard(state, data) {
        state.isToCoArtBoard = data
    },
    setIsAudioing(state, data) {
        state.isAudioing = data
    },
    setIsVideoing(state, data) {
        state.isVideoing = data
    },
}
const state = {
    isToCoArtBoard: false, // 是否在白板协作
    isVideoing: false, //是否正在视频通话
    isAudioing: false, //是否正在语音通话
    sysUsers:'',
    agreeFriendValidate: false, // 同意好友申请
    recentConversation: [], // 最近的会话列表
    currentConversation: {}, // 当前的会话，在白板协作、音视频通话会使用
    allConversation: [], // 所有会话
    allFriends: [], // 所有的好友
    allGroups: [],
    addMemberLis: [],
    rightList: [],
    
    // sysMessage: {}
}
const getters = []


export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
  }