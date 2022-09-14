const saveFriend = (id) => {
    const storage = window.localStorage
    let recentStr = (storage.getItem('coMessager-recentConversation-friend') || '')
    const recentArr = recentStr.split(',')
    if (recentArr.includes(id)) return
    if (typeof(id) == 'undefined' || id == '') return
    recentStr = recentStr ? recentStr + ',' + id : id
    console.log(recentStr)
    storage.setItem('coMessager-recentConversation-friend',recentStr)
}   
/**
 * 将最近会话保存到本地
 * @param {Object} data 
 */
export const saveRecentConversationTolocal = (data) => {
    if (data.conversationType === 'FRIEND') {  
        saveFriend(data.id)
    } else if (data.conversationType === 'GROUP') {
      console.log('saveRecentConversationTolocal---------');
        saveGroup(data.roomId)
    }
}
const saveGroup = (id) => {
  const storage = window.localStorage
  let recentStr = (storage.getItem('coMessager-recentConversation-group') || '')
  const recentArr = recentStr.split(',')
  if (recentArr.includes(id)) return
  if (typeof(id) == 'undefined' || id == '') return
  recentStr = recentStr ? recentStr + ',' + id : id
  storage.setItem('coMessager-recentConversation-group', recentStr)
}

// --------------------------------------------------------------
/**
 * 数组去重
 * @param {Array} arr 
 * @returns 
 */
// export const arrUnique = (arr) => {
//     return [...new Set(arr)]
// }
export const arrUnique = (arr) => {
    const list = [...arr]
    return list.reduce((mult, item) => {
        !mult.includes(item) ? mult.push(item) : null
        return mult
    }, [])
}

/** 将我的好友ID、群聊ID 保存至 localStorage */
export const saveMyFriendsToLocalStorage = (data) => {
    data = JSON.stringify(data)
    window.localStorage.setItem('friends', data)
  }
  export const saveMyGroupToLocalStorage = (data) => {
    if (data.length == 0) return
    data = JSON.stringify(data)
    window.localStorage.setItem('groups', data)
  }

/**
 * 将时间转化为中文的形式：昨天 12：12，12月12日 12：12
 * @param {String | Number} data 
 */
  export const formatDateToZH = (data) => {
    const targetDate = new Date(data)
    const nowTime = new Date(formatDate(new Date())).getTime()
    const targetTime = new Date(formatDate(targetDate)).getTime()
    const daysAgo = (nowTime - targetTime) / 86400000
    const yearsAgo = new Date().getFullYear() - targetDate.getFullYear()
    const year = `${targetDate.getFullYear()}年`
    const monthAndDay = `${formatNumber(targetDate.getMonth() + 1)}月${formatNumber(targetDate.getDate())}日`
    const hourAndMinute = formatNumber(targetDate.getHours()) + ':' + formatNumber(targetDate.getMinutes())
    if (daysAgo < 1) {
        return hourAndMinute
    } else if (1 <= daysAgo && daysAgo < 2) {
        return `昨天 ${hourAndMinute}`
    } else if (yearsAgo < 1) {
        return monthAndDay + ' ' + hourAndMinute
    } else {
        return year + monthAndDay + ' ' + hourAndMinute
    }
  }

/**
 * 格式化日期
 * @param {Date} time
 * @param {String} type
*/
  export function formatDate(time, type = 'YYYY-MM-DD') {
        const y = formatNumber(time.getFullYear())
        const m = formatNumber(time.getMonth() + 1)
        const d = formatNumber(time.getDate())
        const h = formatNumber(time.getHours())
        const mm = formatNumber(time.getMinutes())
        const s = formatNumber(time.getSeconds())
        if (full) {
            return `${y}-${m}-${d} ${h}:${mm}:${s}`
        } else {
            return `${y}-${m}-${d}`
        }
  }

  /**
 * 格式化数字 1 => 01
 * @param {Number} num
 */
function formatNumber(num) {
    let isNumber = isType('Number')
    if (!isNumber(num)) return
    return num >= 10 ? String(num) : '0' + String(num)
  }

  // 判断数据类型
function isType(type) {
    return (arg) => {
      return Object.prototype.toString.call(arg) === `[object ${type}]`
    }
  }

/**
 * 格式化时间
 * @param {Date} time
 * @param {Boolean} full
 */
let full = null
 export function fromatTime(time, full = true) { //
    const y = formatNumber(time.getFullYear())
    const m = formatNumber(time.getMonth() + 1)
    const d = formatNumber(time.getDate())
    const h = formatNumber(time.getHours())
    const mm = formatNumber(time.getMinutes())
    const s = formatNumber(time.getSeconds())
    if (full) {
      return `${y}-${m}-${d} ${h}:${mm}:${s}`
    } else {
      return `22/${m}/${d}`
    }
  }

/**
 * GUID生成
 */
 export function genGuid() {
  function _guid() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
  }

  return (_guid() + _guid() + "-" + _guid() + "-" + _guid() + "-" + _guid() + "-" + _guid() + _guid() + _guid());
}