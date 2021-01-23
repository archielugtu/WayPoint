module.exports = {
  runtimeCompiler: true,
  publicPath: process.env.VUE_APP_BASE_URL,
  chainWebpack: config => {
    // clear the existing images module
    const imagesRule = config.module.rule('images');
    imagesRule.uses.clear();

    imagesRule
      // you can set whatever you want or remove this line completely
      .test(/\.(png|jpe?g|gif|webp)(\?.*)?$/)
      .use('file-loader')
      .loader('file-loader')
      .tap(options => {
        return {
          ...options,
          limit: -1 // no limit
        };
      })
      .end();
  }
};
