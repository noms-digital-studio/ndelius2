import OffenderSearch from './offenderSearch'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'

describe('OffenderSearch component', () => {
  context('search term typed in', () => {
    it('search callback function called with search term', () => {
      const search = stub()
      const searchBox = shallow(<OffenderSearch search={search} searchTerm={'Mr Bean'}
                                                probationAreasFilter={['N01']} searchType={'broad'} />)

      searchBox.find('input').simulate('change', { target: { value: 'Mr Beans' } })

      expect(search.getCall(0).args[0]).to.equal('Mr Beans')
      expect(search.getCall(0).args[1]).to.equal('broad')
      expect(search.getCall(0).args[2]).to.eql(['N01'])
    })
  })
  context('form submitted', () => {
    let search
    let preventDefault
    let searchBox

    beforeEach(() => {
      search = stub()
      preventDefault = stub()
      searchBox = shallow(<OffenderSearch search={search} searchTerm={'Mr Bean'} probationAreasFilter={['N01']}
                                          searchType={'broad'} />)
    })
    it('search callback function called with search term', () => {
      searchBox.find('form').simulate('submit', { preventDefault })

      expect(search.getCall(0).args[0]).to.equal('Mr Bean')
      expect(search.getCall(0).args[1]).to.equal('broad')
      expect(search.getCall(0).args[2]).to.eql(['N01'])
    })
    it('submit default behaviour is prevented', () => {
      searchBox.find('form').simulate('submit', { preventDefault })

      expect(preventDefault).to.be.calledWith()
    })
  })
})
