import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'
import GovUkPhaseBanner from './govukPhaseBanner'

describe('GovUK Phase Banner component', () => {
  let wrapper

  describe('clicking Close link', () => {
    let offenderSummaryViewPrevious
    beforeEach(() => {
      offenderSummaryViewPrevious = stub()
      wrapper = shallow(<GovUkPhaseBanner offenderSummaryViewPrevious={offenderSummaryViewPrevious} offenderId={123} />)
    })

    it('callback called with offenderId', () => {
      wrapper.find('.qa-view-previous').simulate('click')
      expect(offenderSummaryViewPrevious).to.be.calledWith(123)
    })
  })
})
