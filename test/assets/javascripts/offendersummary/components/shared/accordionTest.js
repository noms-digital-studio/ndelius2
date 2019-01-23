import React, { Fragment } from 'react'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import Accordion from './accordion'

describe('Accordion component (shared)', () => {

  let wrapper

  describe('Accordion', () => {

    beforeEach(() => {
      wrapper = shallow(<Accordion label="Offender details" id="1">
        <Fragment>Content goes here</Fragment>
      </Accordion>)
    })

    it('contains label', () => {
      expect(wrapper.find('.govuk-accordion__section-button').text()).to.contain('Offender details')
    })

    it('has heading ID set correctly', () => {
      expect(wrapper.find('#accordion-default-heading-1')).to.have.lengthOf(1)
    })

    it('has content ID set correctly', () => {
      expect(wrapper.find('#accordion-default-content-1')).to.have.lengthOf(1)
    })
  })
})
