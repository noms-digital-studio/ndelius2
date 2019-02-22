import FrameNavigation from './frameNavigation'
import { expect } from 'chai'
import { shallow } from 'enzyme'

describe('FrameNavigation component', () => {
  context('a close navigation event', () => {
    it('postmessage on parent frame called', () => {
      let message
      global.parent.postMessage = (param) => { message = param }
      const navigationData = {
        shouldClose: true,
        action: 'SomePage',
        data: 'value'
      }
      shallow(<FrameNavigation navigate={navigationData} />)

      expect(JSON.parse(message)).to.eql({
        action: 'SomePage',
        data: 'value'
      })
    })
  })
  context('a close navigation event with no data', () => {
    it('postmessage on parent frame called', () => {
      let message
      global.parent.postMessage = (param) => { message = param }
      const navigationData = {
        shouldClose: true,
        action: 'SomePage'
      }
      shallow(<FrameNavigation navigate={navigationData} />)

      expect(JSON.parse(message)).to.eql({
        action: 'SomePage',
        data: null
      })
    })
  })
  context('no navigation event', () => {
    it('postmessage on parent frame not called', () => {
      let message
      global.parent.postMessage = (param) => { message = param }
      const navigationData = {
        shouldClose: false
      }
      shallow(<FrameNavigation navigate={navigationData} />)

      expect(message).to.be.undefined
    })
  })
})
