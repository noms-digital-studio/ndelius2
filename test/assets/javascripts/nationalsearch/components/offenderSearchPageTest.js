import OffenderSearchPage from './offenderSearchPage'
import { expect } from 'chai'
import { shallow } from 'enzyme'

describe('OffenderSearchPage component', () => {
  let offenderSearchPage
  context('First time displaying the application before saved search is checked', () => {
    beforeEach(() => {
      offenderSearchPage = shallow(<OffenderSearchPage firstTimeIn showWelcomeBanner={false}
                                                       reloadRecentSearch={() => {}} />)
    })

    it('does not display the search footer', () => {
      expect(offenderSearchPage.find('SearchFooter').exists()).to.be.false
    })

    it('displays the search results', () => {
      expect(offenderSearchPage.find('Connect(OffenderSearchResults)').exists()).to.be.true
    })
  })

  context('First time displaying the application after saved search is checked', () => {
    beforeEach(() => {
      offenderSearchPage = shallow(<OffenderSearchPage firstTimeIn showWelcomeBanner reloadRecentSearch={() => {}} />)
    })

    it('displays the search footer', () => {
      expect(offenderSearchPage.find('SearchFooter').exists()).to.be.true
    })

    it('does not display the search results', () => {
      expect(offenderSearchPage.find('Connect(OffenderSearchResults)').exists()).to.be.false
    })
  })

  context('Once a search has been completed by the user', () => {
    beforeEach(() => {
      offenderSearchPage = shallow(<OffenderSearchPage firstTimeIn={false} showWelcomeBanner={false}
                                                       reloadRecentSearch={() => {}} />)
    })

    it('does not display the search footer', () => {
      expect(offenderSearchPage.find('SearchFooter').exists()).to.be.false
    })

    it('displays the search results', () => {
      expect(offenderSearchPage.find('Connect(OffenderSearchResults)').exists()).to.be.true
    })
  })
})
