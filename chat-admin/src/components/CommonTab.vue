<template>
  <div class="tabs">
      <!--  -->
    <el-tabs v-model="currentRoute" type="card" closable @tab-remove="removeTab" @tab-click="change">
        <el-tab-pane
            v-for="item in tabList"
            :key="item.label"
            :label="item.label"
            :name="item.name"
            @click.native="changeTab(item)"
        >
        </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import {mapState} from 'vuex'
export default {
    data() {
        return {
            // tabIndex: 2
        }
    },
    computed: {
        ...mapState('app', ['tabList']),
        currentRoute: {
            get() {
                let editableTabsValue = this.$route.name
                return editableTabsValue
            },
            set() {

            }
        }
    },
    
    methods: {
        change(vc) {
            this.$router.push({
                name: vc._props.name
            })
        },
        removeTab(targetName) {
            if (targetName == 'home') return
            let tabs = this.tabList;
            let activeName = this.currentRoute
            if (activeName === targetName) {
                tabs.forEach((tab, index) => {
                    if (tab.name === targetName) {
                        let nextTab = tabs[index + 1] || tabs[index - 1];
                        if (nextTab) {
                            activeName = nextTab.name;
                        }
                    }
                });
            }
            this.$router.push({
                name: activeName
            })
            this.$store.commit('app/closeTab', targetName)
        }
    },
    mounted() {
    }
}
</script>

<style scoped>

</style>