import request from '../../utils/request'

export default {
  // 获取系统信息
  getSystemInfo() {
      return request.get('')
  },

  // 获取所有用户
  getAllUser() {
      return request.get('/chat/getAllUser')
  },
  /**
 * month 表示获取哪一个月的用户注册数量
 */
  getUserBySignTime(month) {
    const {lt, rt} = month
    return request.get(`/sys/getUsersBySignUpTime?lt=${lt}&rt=${rt}`)
  },
  /** 更改用户状态：0；正常，1：冻结，2：注销 */
  changeUserStatus(data) {
    const {uid, status} = data
    return request.get(`/sys/changeUserStatus?uid=${uid}&status=${status}`)
  },
  getUserInfo(id) {
    return request.get(`/user/getUserInfo?uid=${id}`)
  },
}