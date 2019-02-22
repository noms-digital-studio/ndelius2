import Notes from './notes'
import { expect } from 'chai'
import { shallow } from 'enzyme'

describe('Notes component', () => {
  describe('rendering', () => {
    let wrapper

    context('when fetching', () => {
      beforeEach(() => {
        wrapper = shallow(<Notes notes='' fetching error={false} />)
      })

      it('no main content is displayed', () => {
        expect(wrapper.find('.qa-offender-notes').exists()).to.be.false
      })
    })
    context('when finished fetching', () => {
      beforeEach(() => {
        wrapper = shallow(<Notes notes='' fetching={false} error={false} />)
      })

      it('main content is displayed', () => {
        expect(wrapper.find('.qa-offender-notes').exists()).to.be.true
      })
    })
    context('when in error', () => {
      beforeEach(() => {
        wrapper = shallow(<Notes notes='' fetching={false} error />)
      })

      it('no main content is displayed', () => {
        expect(wrapper.find('.qa-offender-notes').exists()).to.be.false
      })
      it('error is displayed', () => {
        expect(wrapper.find('ErrorMessage').exists()).to.be.true
      })
    })
    context('no notes', () => {
      beforeEach(() => {
        wrapper = shallow(<Notes notes={''} fetching={false} error={false} />)
      })
      it('no notes displayed', () => {
        expect(wrapper.find('.qa-offender-notes .moj-card__body p')).to.have.length(1)
        expect(wrapper.find('.qa-offender-notes .moj-card__body').text()).to.equal('')
      })
    })
    context('one note', () => {
      beforeEach(() => {
        wrapper = shallow(<Notes notes={'This is a note'} fetching={false} error={false} />)
      })
      it('one note is displayed', () => {
        expect(wrapper.find('.qa-offender-notes .moj-card__body p')).to.have.length(1)
        expect(wrapper.find('.qa-offender-notes .moj-card__body p').text()).to.equal('This is a note')
      })
    })
    context('many notes', () => {
      beforeEach(() => {
        wrapper = shallow(<Notes
          notes={'This is the first note\n----------------------------\nThis is the second note\n----------------------------\nThis is the third note'}
          fetching={false} error={false} />)
      })
      it('all notes are displayed', () => {
        expect(wrapper.find('.qa-offender-notes .moj-card__body p')).to.have.length(5)
        expect(wrapper.find('.qa-offender-notes .moj-card__body p').at(0).text()).to.equal('This is the first note')
        expect(wrapper.find('.qa-offender-notes .moj-card__body p').at(1).text()).to.equal('----------------------------')
        expect(wrapper.find('.qa-offender-notes .moj-card__body p').at(2).text()).to.equal('This is the second note')
        expect(wrapper.find('.qa-offender-notes .moj-card__body p').at(3).text()).to.equal('----------------------------')
        expect(wrapper.find('.qa-offender-notes .moj-card__body p').at(4).text()).to.equal('This is the third note')
      })
    })
  })
})
