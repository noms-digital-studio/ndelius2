import CloseLink from './closeLink'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'

describe('CloseLink component', () => {
  let wrapper

  describe('clicking Close link', () => {
    let offenderSummaryClose
    beforeEach(() => {
      offenderSummaryClose = stub()
      wrapper = shallow(<CloseLink offenderSummaryClose={offenderSummaryClose} />)
    })

    it('callback called', () => {
      wrapper.find('a').simulate('click')
      expect(offenderSummaryClose).to.be.calledWith()
    })
  })
})
