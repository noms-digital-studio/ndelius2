import PropTypes from 'prop-types'

const MAX_PAGES = 10

const PageSelection = ({pageSize, pageNumber, total, gotoPage, searchTerm}) => (
    <div>
        {shouldDisplay(pageSize, total) &&
        <span>
            {notOnFirstPage(pageNumber) &&
            <span>
                <a id='previous-page-link' className='clickable margin-right' onClick={() => gotoPage(searchTerm, pageNumber - 1)}>&lt; Previous</a>
                <span className='margin-right'>-</span>
            </span>
            }
            {range(Math.min(totalPages(pageSize, total), MAX_PAGES))
                .map(linkPageNumber => <PageLink
                                            key={linkPageNumber}
                                            pageNumber={pageNumber}
                                            linkPageNumber={linkPageNumber}
                                            gotoPage={gotoPage}
                                            searchTerm={searchTerm}/>)
            }
            {notOnLastPage(pageNumber, totalPages(pageSize, total)) &&
            <span>
                <span className="margin-right">-</span>
                <a id='next-page-link' className='clickable' onClick={() => gotoPage(searchTerm, pageNumber + 1)}>Next &gt;</a>
            </span>
            }
        </span>
        }
    </div>
);


PageSelection.propTypes = {
    pageSize: PropTypes.number.isRequired,
    pageNumber: PropTypes.number.isRequired,
    total: PropTypes.number.isRequired,
    gotoPage: PropTypes.func.isRequired
};


const PageLink = ({pageNumber, linkPageNumber, gotoPage, searchTerm}) => {
    if (pageNumber === linkPageNumber) {
        return (<span className='margin-right'>{linkPageNumber}</span>)
    }
    return (<span className='margin-right'><a className='clickable' onClick={() => gotoPage(searchTerm, linkPageNumber)}>{linkPageNumber}</a></span>)
}


const shouldDisplay = (pageSize, total) => total > pageSize
const totalPages = (pageSize, total) => Math.ceil(total / pageSize)
const range = (count) => [...Array(count)].map((v, i) => i + 1)
const notOnFirstPage = (currentPageNumber) => currentPageNumber > 1
const notOnLastPage = (currentPageNumber, totalPages) => currentPageNumber !== totalPages

export default PageSelection;