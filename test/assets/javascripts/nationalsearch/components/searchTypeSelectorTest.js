import SearchTypeSelector from './searchTypeSelector'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'

describe('Search type selector component', () => {
  let searchTypeChanged
  let search

  beforeEach(() => {
    searchTypeChanged = stub()
    search = stub()
  })

  it('selects `No` for `broad` search type', () => {
    const selector = shallow(<SearchTypeSelector
      searchType='broad'
      searchTypeChanged={searchTypeChanged}
      search={search}
      searchTerm=''
      probationAreasFilter={[]}
    />)

    expect(selector.find('#match-all-terms-no').getElement().props.checked).to.be.true
  })

  it('selects `Yes` for `exact` search type', () => {
    const selector = shallow(<SearchTypeSelector
      searchType='exact'
      searchTypeChanged={searchTypeChanged}
      search={search}
      searchTerm=''
      probationAreasFilter={[]}
    />)

    expect(selector.find('#match-all-terms-yes').getElement().props.checked).to.be.true
  })

  it('calls search and searchTypeChanged when `Yes` is clicked', () => {
    const selector = shallow(<SearchTypeSelector
      searchType='broad'
      searchTypeChanged={searchTypeChanged}
      search={search}
      searchTerm='foo'
      probationAreasFilter={[]}
    />)

    selector.find('#match-all-terms-yes').simulate('change', { target: { value: 'exact' } })

    expect(searchTypeChanged).to.be.calledWith('exact')
    expect(search).to.be.calledWith('foo', 'exact', [])
  })

  it('calls search and searchTypeChanged when `No` is clicked', () => {
    const selector = shallow(<SearchTypeSelector
      searchType='exact'
      searchTypeChanged={searchTypeChanged}
      search={search}
      searchTerm='foo'
      probationAreasFilter={[]}
    />)

    selector.find('#match-all-terms-no').simulate('change', { target: { value: 'broad' } })

    expect(searchTypeChanged).to.be.calledWith('broad')
    expect(search).to.be.calledWith('foo', 'broad', [])
  })
})
