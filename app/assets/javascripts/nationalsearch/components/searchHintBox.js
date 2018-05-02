import PropTypes from 'prop-types';

const SearchHintBox = ({hint}) => (
    <p className="margin-top margin-bottom large">
        <span className="search-hint">
            <span className="font-medium">{hint}</span>
        </span>
    </p>
);

SearchHintBox.propTypes = {
    hint: PropTypes.string
};

export default SearchHintBox;