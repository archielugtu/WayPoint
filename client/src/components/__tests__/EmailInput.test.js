/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {mount, createLocalVue} from '@vue/test-utils'
import Vuelidate from 'vuelidate'
import VueRouter from 'vue-router'
import EmailInput from "../EmailInput";

const localVue = createLocalVue()

localVue.use(VueRouter)
const router = new VueRouter()

localVue.use(Vuelidate)

const email = "curly@braces.com"

describe('Show error when email empty', () => {

  const wrapper = mount(EmailInput, {
    localVue,
    router,
    propsData: {
      default: email,
      required: true
    }
  })

  const vm = wrapper.vm

  it('Field showing error when empty', async () => {
    expect(wrapper.find('#email-error').exists()).toBe(false)
    expect(wrapper.vm.email).toBe(email)
    vm.email = ""
    vm.$v.$touch()
    await vm.$nextTick()
    expect(wrapper.find('#email-error').isVisible()).toBe(true)
  })
})


describe('Show error when email invalid', () => {

  const wrapper = mount(EmailInput, {
    localVue,
    router,
    propsData: {
      default: email,
      required: true
    }
  })

  const vm = wrapper.vm

  it('Field showing error when empty', async () => {
    expect(wrapper.find('#email-error').exists()).toBe(false)
    expect(wrapper.vm.email).toBe(email)
    vm.email = "test"
    vm.$v.$touch()
    await vm.$nextTick()
    expect(wrapper.find('#email-error').isVisible()).toBe(true)
  })
})
