/**
 * 获取设备类型
 */
export const getDeviceType = () => {
  const reg = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
  return reg.test(navigator.userAgent) ? 'Mobile' : 'Desktop'
}

/**
 * 格式化日期
 * @param {Date} time
 * @param {String} type
 */
export function formatDate(time, type = 'YYYY-MM-DD') { //
  const y = formatNumber(time.getFullYear())
  const m = formatNumber(time.getMonth() + 1)
  const d = formatNumber(time.getDate())
  const h = formatNumber(time.getHours())
  const mm = formatNumber(time.getMinutes())
  const s = formatNumber(time.getSeconds())
  if (type === 'YYYY-MM-DD HH-MM-SS') {
    return `${y}-${m}-${d} ${h}:${mm}:${s}`
  } else if (type === 'YYYY-MM-DD') {
    return `${y}-${m}-${d}`
  } else if (type === 'YYYY-MM') {
    return `${y}-${m}`
  } else if (type === 'HH-MM-SS') {
    return `${h}:${mm}:${s}`
  } else if (type === 'HH') {
    return `${h}`
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