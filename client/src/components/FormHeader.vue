<template>
  <div>
    <div class="field is-horizontal">
      <div class="field-label is-normal"></div>
      <div class="field-body">
        <h1 class="title">{{ title }}</h1>
      </div>
    </div>

    <div class="field is-horizontal">
      <div class="field-label is-normal"></div>
      <div class="field-body">
        <h3 class="subtitle">{{ subtitle }}</h3>
      </div>
    </div>
  </div>
</template>


<script>
  export default {
    name: "FormHeader",
    props: ["title", "subtitle"]
  };
</script>


<style scoped>
</style>

