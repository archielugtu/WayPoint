<template>
  <div>
    <section :style="{ backgroundImage: `url(${require(`@/assets/splash.webp`)})` }" class="hero is-link is-fullheight header-image">
      <div class="hero-body">
        <div class="container">
          <h1 id="splash-text" class="has-text-centered is-unselectable title splash-text">Waypoint</h1>
          <h3 class="has-text-centered is-unselectable subtitle splash-subtitle">te ora te hoк tere</h3>
        </div>
      </div>
    </section>
  </div>
</template>


<script>
  export default {
    name: "Home",
  };
</script>


<style scoped>
  .header-image {
    background-position: center center;
    background-repeat: no-repeat;
    background-size: cover;
  }

  .splash-text {
    font-family: "Pacifico", serif;
    text-shadow: 2px 1px #543c2c;
    font-size: calc(58px + (78 - 58) * ((100vw - 300px) / (1600 - 300)));
    margin-bottom: 40px;
  }

  .splash-subtitle {
    font-family: "Montserrat", serif;
    font-size: calc(29px + (39 - 29) * ((100vw - 300px) / (1600 - 300)));
  }
</style>

