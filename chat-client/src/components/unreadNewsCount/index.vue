<template>
  <div>
      <div class="container">
          {{count}}
      </div>
  </div>
</template>

<script>
export default {
    props:['count']

}
</script>

<style scoped>
.container {
    height: 16px;
    width: 16px;
    background-color: red;
    color: white;
    font-size: 10px;
    border-radius: 8px;
    text-align: center;
    position: absolute;
    top: 4px;
    left: 44px;
}

</style>