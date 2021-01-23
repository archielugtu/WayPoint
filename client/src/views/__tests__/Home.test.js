/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {mount, createLocalVue} from '@vue/test-utils'
import VueRouter from 'vue-router'
import Home from "../Home"

const localVue = createLocalVue()

localVue.use(VueRouter)
const router = new VueRouter()

describe('Page Display', () => {

  const wrapper = mount(Home, {
    localVue,
    router,
  })

  it('Renders splash text', () => {
    expect(wrapper.find('#splash-text').isVisible()).toBe(true)
  })

})
