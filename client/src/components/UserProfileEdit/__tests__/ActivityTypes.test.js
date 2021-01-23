/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {mount, createLocalVue} from '@vue/test-utils'
import ActivityTypes from "../ActivityTypes"
import api from '@/Api'
import Buefy from 'buefy'

const localVue = createLocalVue()
localVue.use(Buefy)

const $route = {
  params: {
    userId: 1
  }
}

const userData = {
  activities: [
    "Bob Rafting",
    "Capoeira"
  ]
}

// Create a mock for API call to api.getUserProfile and api.getUserActivitiesById
jest.mock('@/Api')

// Give fake mock data for API calls
api.getActivityTypes.mockResolvedValue(
  Promise.resolve({
    data: {
      activities: ["Quad Biking", "Water WalkerZ"]
    }
  }
))

describe('Page Display', () => {

  let wrapper;

  beforeEach(() => {
    wrapper = mount(ActivityTypes, {
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

  it('displays the activity type input box', () => {
    expect(wrapper.find('#activity-types').isVisible()).toBe(true)
  })

})
