/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {mount, createLocalVue} from '@vue/test-utils'
import Passports from "../Passports"
import Buefy from 'buefy'
import axios from 'axios'

// Create a mock for API call to api.getUserProfile and api.getUserActivitiesById
jest.mock('axios')

const localVue = createLocalVue()
localVue.use(Buefy)

const userData = {
  passports: {
    code: "JPN",
    flag: "https://restcountries.eu/data/jpn.svg",
    name: "Japan"
  }
}

const $route = {
  params: {
    userId: 1
  }
}

describe('Page Display', () => {

  let wrapper;

  // Give fake mock data for axios call to restcountries.eu
  axios.get.mockResolvedValue(
    Promise.resolve({
      data: [
        {
          code: "JPN",
          flag: "https://restcountries.eu/data/jpn.svg",
          name: "Japan"
        }
      ]
    }))

  beforeEach(() => {
    wrapper = mount(Passports, {
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

  it('shows the label', () => {
    expect(wrapper.find('#passports').isVisible()).toBe(true)
  })

  it('shows the add country button', () => {
    expect(wrapper.find('#add-country-btn').isVisible()).toBe(true)
  })

  it('shows the Japan passport flag image', () => {
    expect(wrapper.find('figure.image.country-flag').isVisible()).toBe(true)
  })

  it('shows the Japan passport text', () => {
    expect(wrapper.find('input.country-name').isVisible()).toBe(true)
  })

  it('shows the delete passport button', () => {
    expect(wrapper.find('button.button.is-danger').isVisible()).toBe(true)
  })

})
