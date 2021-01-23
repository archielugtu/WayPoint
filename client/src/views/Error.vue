<template>
  <div class="hero-body">
    <div class="container">
      <div class="columns is-centered">
        <div class="column is-half">
          <nav class="level">
            <div class="level-left">
              <div class="level-item">
                <figure class="error-image image">
                  <img src="./assets/error.webp" alt="error!"/>
                </figure>
              </div>
              <div class="level-item error-content">
                <div>
                  <h1 class="title is-1">Oh no!</h1>
                  <br />
                  <p class="content is-medium">
                    Looks like you've found a bad detour...
                    <br />Let's get you back home!
                  </p>
                  <router-link :to="{name: 'home'}" class="button is-primary is-outlined">
                    <strong>Home</strong>
                  </router-link>
                </div>
              </div>
            </div>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "Error"
  };
</script>

<style scoped>
  .error-image {
    width: 256px;
    height: 256px;
  }

  .error-content {
    margin-left: 24px;
  }
</style>
