<template>
  <div>
    <section>
      <div class="hero-body">
        <div class="container">
          <h1 id="splash-text" class="has-text-centered is-unselectable title">Activity is restricted to certain users only</h1>
          <img id="error-img" class="filtered" :src="require(`@/assets/activity-error.svg`)" alt="dashboard image" />
        </div>
      </div>
    </section>
  </div>
</template>

<script>
  export default {
    name: "ActivityErrorPage"
  }
</script>

<style scoped>

  #error-img {
    height: 400px;
    display: block;
    margin: auto;
    margin-bottom: 2rem;
  }
</style>