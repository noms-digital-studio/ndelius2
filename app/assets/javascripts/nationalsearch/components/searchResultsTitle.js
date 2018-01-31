import PropTypes from "prop-types";

const SearchResultsTitle = ({pageNumber, pageSize, total, resultsReceived}) => {
    if (resultsReceived === false) {
        return (<div/>);
    }
    if (total === 0) {
        return (
            <h2 className="heading-medium margin-top medium"><span>0 results found</span></h2>
        )
    }
    const resultPlural = total === 1 ? 'result' : 'results'
    if (numberOfPages(pageSize, total) === 1) {
        return (
            <h2 className="heading-medium margin-top medium">
                <span>{`${total} ${resultPlural} found`}</span>
            </h2>
        )
    }
    return (
        <h2 className="heading-medium margin-top medium">
            <span>{`${total} ${resultPlural} found, showing ${fromResult(pageNumber, pageSize)} to ${toResult(pageNumber, pageSize, total)}`}</span>
        </h2>
    )
}

const fromResult = (pageNumber, pageSize) => ((pageNumber - 1) * pageSize) + 1
const toResult = (pageNumber, pageSize, total) => Math.min(total, pageNumber * pageSize)
const numberOfPages = (pageSize, total) => Math.ceil(total / pageSize)

SearchResultsTitle.propTypes = {
    pageNumber: PropTypes.number.isRequired,
    pageSize: PropTypes.number.isRequired,
    total: PropTypes.number.isRequired,
    resultsReceived: PropTypes.bool.isRequired,
};


export default SearchResultsTitle;