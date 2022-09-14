<template>
  <div class="video-call-container">
      <div class="top"><span @click="close">X</span></div>
      <!--  -->
      <div class="to-who" v-if="false">
          <img src="" alt="">
          <div>
              <i>我就是太阳</i>
              <span>等待对方接受邀请...</span>
          </div>
      </div>
      <!--  -->
      <div class="video-box">
          <video src=""  id="received_video" style="width: 100%;height:100%;" autoplay></video>
          <div class="handUp">
              <i class="el-icon-phone-outline"></i>
              <span>挂断</span>
          </div>
      </div>
      <video src="" id="local_video" class="myself-s" autoplay ></video>
  </div>
</template>

<script>
export default {
    // ,
    props: ["state", "webRtcType"],
    data() {
        return {
            loadingText: true,
            conversation: {},
            localStream: '', //本地视频流
            isToPeer: false, // 是否建立了 P2P 连接
            peer: null,
            offerOption: {
                offerToReceiveAudio: 1,
                offerToReceiveVideo: 1
            },
        }
    },
    computed: {
        currentConversation: {
        get() {
            return this.$store.state.app.currentConversation
        },
        set() {
        
        }
    },

    },
    methods: {
        close() {
            this.$emit('closeVideo')
        },
        apply() {
            console.log(this.currentConversation);
            this.currentConversation.webRtcType = this.webRtcType
            this.$socket.emit("apply", this.currentConversation)
        },
        reply() {
            this.currentConversation.webRtcType = this.webRtcType
            console.log(this.currentConversation.webRtcType);
            this.$socket.emit('reply',this.currentConversation)
        },
        async createP2P(data) {
            await this.createMedia(data)
        },
        async createMedia(data) {
            const that = this
            try {
                const webRtcType = that.webRtcType
                console.log(webRtcType);
                let constraints = null
                console.log('that');
                console.log(webRtcType);
                if (webRtcType === 'video') {
                    constraints = {
                    audio: true,
                    video: true
                    }
                } else {
                    constraints = {
                    audio: true,
                    video: false
                    }
                }
                // streamtep =  await navigator.mediaDevices.getUserMedia(constraints)
                let streamtep = await navigator.mediaDevices.getUserMedia(constraints)
                console.log(streamtep);
                that.localStream = streamtep
                let localVideo = document.querySelector('#local_video')
                localVideo.srcObject = streamtep
            } catch(e) {
                console.log("getUserMedia: ", e)
            }
            await this.initPeer(data) // 获取到媒体流后，调用函数初始化 RTCPeerConnection
        },
        initPeer(data) {
            const that = this
            console.log('that');
            console.log(that);
            console.log(this);
            let PeerConnection =
            window.RTCPeerConnection ||
            window.mozRTCPeerConnection ||
            window.webkitRTCPeerConnection
            that.peer = new PeerConnection()
            that.peer.addStream(that.localStream)
            that.peer.onicecandidate = function(event) {
               if (event.candidate) {
                    // console.log("sdp：", event.candidate)
                    data.sdp = event.candidate
                    that.$socket.emit("1v1ICE", data)
                } else {
                    console.log("ICE收集已经完成")
                }
                that.peer.onaddstream = event => {
                    that.isToPeer = true
                    let receivedVideo = document.querySelector("#received_video")
                    receivedVideo.srcObject = event.stream
                }
            }
        },
        async createOffer(data) {
            try {
                let offer = await this.peer.createOffer(this.offerOption)
                await this.peer.setLocalDescription(offer)
                data.sdp = offer
                this.$socket.emit("1v1offer",data)
            } catch(e) {
                console.log("createOffer: ", e);
            }
        },
        async onOffer(data) {
            try {
                await this.peer.setRemoteDescription(data.sdp)
                let answer = await this.peer.createAnswer()
                await this.peer.setLocalDescription(answer)
                data.sdp = answer
                this.$socket.emit("1v1answer", data)
            } catch(e) {
                console.log("onOffer: ", e)
            }
        },
        async onAnswer(data) {
            try {
                await this.peer.setRemoteDescription(data.sdp) // 呼叫端设置远程 answer 描述
            } catch(e) {
                console.log("onAnswer: ", e)
            }
        },
        async onIce(data) {
            try {
                await this.peer.addIceCandidate(data.sdp) // 设置远程 ICE
            } catch(e) {
                console.log("onAnswer: ", e)
            }
        }

    },
    sockets: {
        async reply(data) {
            if (data.type && (data.type == 'disagree' || data.type == 'busy')) return
            console.log('wo到底有没有执行');
            console.log(data);
            await this.createP2P(data)
            this.createOffer(data)
        },
        '1v1offer'(data) {
            this.onOffer(data)
        },
        '1v1answer'(data) {
            this.onAnswer(data)
        },
        '1v1ICE'(data) {
            this.onIce(data)
        },

    },
    created() {
        
    },
    async mounted() {
        // let localVideo = document.querySelector('#local_video')
        // let cameraStream = await navigator.mediaDevices.getUserMedia({
        //     video: true,
        //     audio: false
        // })
        // localVideo.srcObject = cameraStream
        if (this.state == 'apply') {
            this.apply()
        } else if (this.state == 'reply') {
            this.currentConversation.webRtcType = this.webRtcType
            console.log(this.currentConversation.webRtcType);
            await this.createP2P(this.currentConversation)
            this.reply()
        }
    }

}
</script>

<style scoped>
.video-call-container {
    width: 384px;
    height: 680px;
    position: fixed;
    top: 0px;
    left: 5px;
    z-index: 1000;
    background-color: aqua;
}
.top {
    display: flex;
    justify-content: flex-end;
    background-color: #191919;
    color: #848484;
}
.top span {
    width: 24px;
    height: 24px;
    text-align: center;
}
.top span:hover {
    background-color: #fa5151;
}
/*  */
.to-who {
    width: 200px;
    height: 60px;
    position: absolute;
    top: 40px;
    left: 16px;
    display: flex;
    font-size: 16px;
    color: #eee;
    background-color: #fa5151;
}
.to-who div {
    margin-left: 12px;
}
.to-who img {
    width: 60px;
    height: 60px;
}
.to-who i {
    display: block;
    margin-top: 8px;
    margin-bottom: 6px;
}
.to-who span {
    font-size: 14px;
    color: #f0f0f0;
}
/*  */
.video-box {
    width: 100%;
    height: 100%;
    background-color: #848484;
}

.handUp {
    position: absolute;
    bottom: 28px;
    left: 168px;
}
.handUp i {
    display: block;
    font-size: 38px;
}
.handUp span{
    text-align: center;
}
/*  */
.myself-s {
    width: 30%;
    height: 20%;
    position: absolute;
    top: 44px;
    left: 0px;
    /* z-index: 1001; */
}
</style>