/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {mount, createLocalVue} from '@vue/test-utils'
import Navbar from "../Navbar";
import Vuex from 'vuex'
import Buefy from 'buefy'

const localVue = createLocalVue()
localVue.use(Vuex)
localVue.use(Buefy)

const stubs = ['router-link']

const mocks = {
  $route: {
    params: {
      userId: 1
    }
 },
  $router: {
    push: jest.fn(),
    currentRoute: {
      path: "",
      name: ""
    }
  }
}

describe('Navbar display when logged out', () => {

  let wrapper
  let store

  beforeEach(() => {
    store = new Vuex.Store({
      state: {
        isLoggedIn: false
      },
      getters: {
        hasAdminPrivileges: (state) => false
      }
    })

    wrapper = wrapper = mount(Navbar, {
      localVue,
      store,
      mocks,
      stubs
    })
  })

  it('has a Sign up button', async () => {
    expect(wrapper.find('#signup').exists()).toBe(true)
  })

  it('has a Login button', async () => {
    expect(wrapper.find('#login').exists()).toBe(true)
  })
})


describe('Navbar display when logged in', () => {

  let wrapper
  let store

  beforeEach(() => {
    store = new Vuex.Store({
      state: {
        isLoggedIn: true
      },
      getters: {
        hasAdminPrivileges: (state) => false
      }
    })

    wrapper = wrapper = mount(Navbar, {
      localVue,
      store,
      mocks,
      stubs
    })
  })

  it('has a Logout button', async () => {
    expect(wrapper.find('#logout').exists()).toBe(true)
  })

  it('has a Profile button', async () => {
    expect(wrapper.find('#profile').exists()).toBe(true)
  })
})

describe('Navbar display when logged in as admin', () => {

  let wrapper
  let store

  beforeEach(() => {
    store = new Vuex.Store({
      state: {
        isLoggedIn: true,
        isAdmin: true
      },
      getters: {
        hasAdminPrivileges: (state) => true
      }
    })

    wrapper = wrapper = mount(Navbar, {
      localVue,
      store,
      mocks,
      stubs
    })
  })

  it('has a Logout button', async () => {
    expect(wrapper.find('#logout').exists()).toBe(true)
  })

  it('has a Profile button', async () => {
    expect(wrapper.find('#profile').exists()).toBe(true)
  })

  it('has a Admin Dashboard button', async () => {
    expect(wrapper.find('#dashboard').exists()).toBe(true)
  })
})

