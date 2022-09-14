import request from '../../utils/request'

export default {
    // 管理员登录
    login(data) {
        return request.post('/chat/superLogin', data)
    }
}