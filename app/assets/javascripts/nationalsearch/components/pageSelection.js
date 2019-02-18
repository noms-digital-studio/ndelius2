import React from 'react'
import PropTypes from 'prop-types'
import {range} from '../../helpers/streams'

const MAX_PAGES = 10

const PageSelection = ({pageSize, pageNumber, total, gotoPage, searchTerm, probationAreasFilter, searchType}) => {
    const PageLink = ({linkPageNumber, tabIndex}) => {
        if (pageNumber === linkPageNumber) {
            return (<span className='margin-right'>{linkPageNumber}</span>)
        }
        return (<span className='margin-right'>
            <a  tabIndex={tabIndex} href='#offender-results'
                title={`Page ${linkPageNumber}`}
                className='clickable'
                onClick={() => gotoPage(searchTerm, searchType, probationAreasFilter, linkPageNumber)}>{linkPageNumber}</a></span>)
    }


    return (
    <div role='navigation'>
        {shouldDisplay(pageSize, total) &&
        <span>
            {notOnFirstPage(pageNumber) &&
            <span>
                <a tabIndex="1" href='#offender-results' id='previous-page-link' title="Previous Page"
                   className='clickable margin-right'
                   onClick={() => gotoPage(searchTerm, searchType, probationAreasFilter, pageNumber - 1)}>&lt; Previous</a>
                <span className='margin-right'>-</span>
            </span>
            }
            {range(Math.min(totalPages(pageSize, total), MAX_PAGES))
                .map(linkPageNumber => <PageLink
                                            key={linkPageNumber}
                                            linkPageNumber={linkPageNumber}
                                            tabIndex="1"/>)
            }
            {notOnLastPage(pageNumber, totalPages(pageSize, total)) &&
            <span>
                <span className="margin-right">-</span>
                <a tabIndex="1" href='#offender-results' id='next-page-link' title="Next Page"
                   className='clickable'
                   onClick={() => gotoPage(searchTerm, searchType, probationAreasFilter, pageNumber + 1)}>Next &gt;</a>
            </span>
            }
        </span>
        }
    </div>
)};


PageSelection.propTypes = {
    pageSize: PropTypes.number.isRequired,
    pageNumber: PropTypes.number.isRequired,
    total: PropTypes.number.isRequired,
    gotoPage: PropTypes.func.isRequired,
    searchTerm: PropTypes.string.isRequired,
    probationAreasFilter: PropTypes.arrayOf(PropTypes.string).isRequired,
    searchType: PropTypes.string.isRequired
};



const shouldDisplay = (pageSize, total) => total > pageSize
const totalPages = (pageSize, total) => Math.ceil(total / pageSize)
const notOnFirstPage = (currentPageNumber) => currentPageNumber > 1
const notOnLastPage = (currentPageNumber, totalPages) => currentPageNumber !== totalPages

export default PageSelection;
