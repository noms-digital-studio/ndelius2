import PropTypes from "prop-types";

const SearchResultsTitle = ({results, searchTerm}) => {
    if (searchTerm === '') {
        return (<div/>);
    }
    if (results.length === 0) {
        return (
            <h2 className="heading-medium margin-top medium"><span>0 results found</span></h2>
        )
    }
    return (
        <h2 className="heading-medium margin-top medium"><span>{`${results.length} results found, showing 1 to ${results.length}`}</span></h2>
    )
}


SearchResultsTitle.propTypes = {
    results: PropTypes.array.isRequired,
    searchTerm: PropTypes.string.isRequired,
};


export default SearchResultsTitle;