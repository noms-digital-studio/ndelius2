import PageSelection from './pageSelection'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'

describe('PageSelection component', () => {
  let gotoPage
  beforeEach(() => {
    gotoPage = stub()
  })
  describe(('rendering of links'), () => {
    context('no results have been found', () => {
      it('no text rendered', () => {
        const pages = shallow(<PageSelection pageSize={10} pageNumber={0} total={0} gotoPage={gotoPage}
                                             searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        expect(pages.text()).to.equal('')
      })
    })

    context('all results fit on one page', () => {
      it('no text rendered', () => {
        const pages = shallow(<PageSelection pageSize={10} pageNumber={1} total={10} gotoPage={gotoPage}
                                             searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        expect(pages.text()).to.equal('')
      })
    })

    context('Results don\'t fit on one page', () => {
      it('at least page numbers 1 and 2 rendered', () => {
        const pages = shallow(<PageSelection pageSize={10} pageNumber={1} total={11} gotoPage={gotoPage}
                                             searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)

        expect(allPageNumbers(pages)).to.contains('1').and.contains('2')
      })
    })

    context('Results span exactly 3 pages', () => {
      it('at least page numbers 1, 2 and 3 rendered', () => {
        const pages = shallow(<PageSelection pageSize={3} pageNumber={1} total={9} gotoPage={gotoPage}
                                             searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        expect(allPageNumbers(pages)).to.contains('1').and.contains('2').and.contains('3').and.not.contains('4')
      })

      context('when on first', () => {
        let pages
        beforeEach(() => {
          pages = shallow(<PageSelection pageSize={3} pageNumber={1} total={9} gotoPage={gotoPage}
                                         searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        })
        it('next link is rendered but not previous', () => {
          expect(pages.text()).to.contains('Next').and.not.contains('Previous')
        })
        it('page 1 is not a link but 2 and 3 are links', () => {
          expect(hasLinkForPage(pages, 1)).to.equal(false)
          expect(hasLinkForPage(pages, 2)).to.equal(true)
          expect(hasLinkForPage(pages, 3)).to.equal(true)
        })
      })

      context('when on middle page', () => {
        let pages
        beforeEach(() => {
          pages = shallow(<PageSelection pageSize={3} pageNumber={2} total={9} gotoPage={gotoPage}
                                         searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        })
        it('next and previous links are both rendered', () => {
          expect(pages.text()).to.contains('Next').and.contains('Previous')
        })
        it('page 2 is not a link but 1 and 3 are links', () => {
          expect(hasLinkForPage(pages, 1)).to.equal(true)
          expect(hasLinkForPage(pages, 2)).to.equal(false)
          expect(hasLinkForPage(pages, 3)).to.equal(true)
        })
      })

      context('when on last page', () => {
        let pages
        beforeEach(() => {
          pages = shallow(<PageSelection pageSize={3} pageNumber={3} total={9} gotoPage={gotoPage}
                                         searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        })
        it('previous link is rendered but not next', () => {
          expect(pages.text()).to.contains('Previous').and.not.contains('Next')
        })
        it('page 3 is not a link but 1 and 2 are links', () => {
          expect(hasLinkForPage(pages, 1)).to.equal(true)
          expect(hasLinkForPage(pages, 2)).to.equal(true)
          expect(hasLinkForPage(pages, 3)).to.equal(false)
        })
      })
    })

    context('Results span just over 3 pages', () => {
      it('at least page numbers 1, 2, 3 and 4 rendered', () => {
        const pages = shallow(<PageSelection pageSize={3} pageNumber={1} total={10} gotoPage={gotoPage}
                                             searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        expect(allPageNumbers(pages)).to.contains('1').and.contains('2').and.contains('3').and.contains('4')
      })
    })

    context('Results span over 10 pages', () => {
      it('only pages up to 10 are displayed', () => {
        const pages = shallow(<PageSelection pageSize={10} pageNumber={1} total={200} gotoPage={gotoPage}
                                             searchTerm={'Mr Bean'} probationAreasFilter={[]} searchType='broad' />)
        expect(allPageNumbers(pages)).to.contains('1').and.contains('10').and.not.contains('11')
      })
    })
  })

  describe('clicking links', () => {
    it('clicking a page link will call gotoPage with page number', () => {
      const pages = shallow(<PageSelection pageSize={3} pageNumber={1} total={9} gotoPage={gotoPage}
                                           searchTerm={'Mr Bean'} probationAreasFilter={['N01']}
                                           searchType='broad' />)

      clickLinkForPage(pages, 2)
      expect(gotoPage.getCall(0).args[0]).to.equal('Mr Bean')
      expect(gotoPage.getCall(0).args[1]).to.equal('broad')
      expect(gotoPage.getCall(0).args[2]).to.eql(['N01'])
      expect(gotoPage.getCall(0).args[3]).to.equal(2)

      clickLinkForPage(pages, 3)
      expect(gotoPage.getCall(1).args[0]).to.equal('Mr Bean')
      expect(gotoPage.getCall(1).args[1]).to.equal('broad')
      expect(gotoPage.getCall(1).args[2]).to.eql(['N01'])
      expect(gotoPage.getCall(1).args[3]).to.equal(3)
    })

    it('clicking next page link will call gotoPage with current page number + 1', () => {
      let pages = shallow(<PageSelection pageSize={3} pageNumber={1} total={9} gotoPage={gotoPage}
                                         searchTerm={'Mr Bean'} probationAreasFilter={['N01']} searchType='broad' />)
      clickNextLink(pages)
      expect(gotoPage.getCall(0).args[0]).to.equal('Mr Bean')
      expect(gotoPage.getCall(0).args[1]).to.equal('broad')
      expect(gotoPage.getCall(0).args[2]).to.eql(['N01'])
      expect(gotoPage.getCall(0).args[3]).to.equal(2)

      pages = shallow(<PageSelection pageSize={3} pageNumber={2} total={9} gotoPage={gotoPage}
                                     searchTerm={'Mr Bean'} probationAreasFilter={['N01']} searchType='broad' />)
      clickNextLink(pages)
      expect(gotoPage.getCall(1).args[0]).to.equal('Mr Bean')
      expect(gotoPage.getCall(1).args[1]).to.equal('broad')
      expect(gotoPage.getCall(1).args[2]).to.eql(['N01'])
      expect(gotoPage.getCall(1).args[3]).to.equal(3)
    })

    it('clicking previous page link will call gotoPage with current page number 1 1', () => {
      let pages = shallow(<PageSelection pageSize={3} pageNumber={3} total={9} gotoPage={gotoPage}
                                         searchTerm={'Mr Bean'} probationAreasFilter={['N01']} searchType='broad' />)
      clickPreviousLink(pages)
      expect(gotoPage.getCall(0).args[0]).to.equal('Mr Bean')
      expect(gotoPage.getCall(0).args[1]).to.equal('broad')
      expect(gotoPage.getCall(0).args[2]).to.eql(['N01'])
      expect(gotoPage.getCall(0).args[3]).to.equal(2)

      pages = shallow(<PageSelection pageSize={3} pageNumber={2} total={9} gotoPage={gotoPage}
                                     searchTerm={'Mr Bean'} probationAreasFilter={['N01']} searchType='broad' />)
      clickPreviousLink(pages)
      expect(gotoPage.getCall(1).args[0]).to.equal('Mr Bean')
      expect(gotoPage.getCall(1).args[1]).to.equal('broad')
      expect(gotoPage.getCall(1).args[2]).to.eql(['N01'])
      expect(gotoPage.getCall(1).args[3]).to.equal(1)
    })
  })
})

const allPageNumbers = wrapper => wrapper.find('PageLink').reduce((text, link) => text + link.prop('linkPageNumber'), '')
const findLinkForPage = (wrapper, linkPageNumber) => wrapper.find({ linkPageNumber }).dive().find('a')
const hasLinkForPage = (wrapper, linkPageNumber) => findLinkForPage(wrapper, linkPageNumber).exists()
const clickLinkForPage = (wrapper, linkPageNumber) => findLinkForPage(wrapper, linkPageNumber).simulate('click')
const clickNextLink = wrapper => wrapper.find('#next-page-link').simulate('click')
const clickPreviousLink = wrapper => wrapper.find('#previous-page-link').simulate('click')
