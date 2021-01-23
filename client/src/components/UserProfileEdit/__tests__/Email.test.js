/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {mount, createLocalVue} from '@vue/test-utils'
import Email from "../Email"
import Vuelidate from 'vuelidate'
import Buefy from 'buefy'

const localVue = createLocalVue()
localVue.use(Buefy)
localVue.use(Vuelidate)

const userData = {
  primary_email: {
    address: "kaguya@shuchiingakuin.com",
    isPrimary: true,
    userEmailID: 4
  },
  additional_email: [{
    address: "shinomiya@shimiyazaibatsu",
    isPrimary: false,
    userEmailID: 5
  }]
}

const $route = {
  params: {
    userId: 1
  }
}

describe('Page Display', () => {

  let wrapper;

  beforeEach(() => {
    wrapper = mount(Email, {
      localVue,
      stubs: ['router-link'],
      mocks: {
        $route: $route
      },
      propsData: {
        userData: userData
      }
    })
  })

  it('shows the email label', () => {
    expect(wrapper.find('#email').exists()).toBe(true)
    expect(wrapper.find('#email').isVisible()).toBe(true)
  })

  it('shows user\'s primary email', () => {
    expect(wrapper.find('input.input').exists()).toBe(true)
  })

  it('shows primary email label', () => {
    expect(wrapper.find('#primary-email-lbl').exists()).toBe(true)
    expect(wrapper.find('#primary-email-lbl').isVisible()).toBe(true)
  })

  it('shows add additional email button', () => {
    expect(wrapper.find('#add-email-btn').exists()).toBe(true)
    expect(wrapper.find('#add-email-btn').isVisible()).toBe(true)
  })

  it('shows add button to delete non primary email', () => {
    expect(wrapper.find('em.fa-times').exists()).toBe(true)
    expect(wrapper.find('em.fa-times').isVisible()).toBe(true)
  })

  it('shows the button to change primary email', () => {
    expect(wrapper.find('em.fa-check').exists()).toBe(true)
    expect(wrapper.find('em.fa-check').isVisible()).toBe(true)
  })

})
