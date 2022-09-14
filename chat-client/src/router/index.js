import VueRouter from 'vue-router'


//引入组件
import MyLogin from '../views/MyLogin'
import MyMain from '../views/MyMain'
import LittleInfoPanel from '@/components/LittleInfoPanel'



//创建并暴露一个路由器
export default new VueRouter({
	routes:[
		//登录页
		{
			path:'/',
			name:'MyLogin',
			component: MyLogin
		},
		//首页
		{
			path:'/main',
			name:'MyMain',
			component:MyMain,
			// children:[
			// 	{
			// 		path:'strangerInfo',
			// 		component:LittleInfoPanel
			// 	}
			// ]
		}
	]
})