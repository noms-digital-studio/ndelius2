import PropTypes from "prop-types";

const OffenderSearch = ({searchTerm, onClick}) => (
    <div>
        <input className="form-control padded" value={searchTerm} onChange={(event) => onClick(event.target.value)} placeholder="Find names, addresses, date of birth, CRN and more..." />
    </div>
);

OffenderSearch.propTypes = {
    searchTerm: PropTypes.string.isRequired,
    onClick: PropTypes.func.isRequired
};


export default OffenderSearch;