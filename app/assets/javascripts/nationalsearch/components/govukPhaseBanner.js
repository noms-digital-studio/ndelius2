import LegacySearchLink from '../containers/legacySearchLinkContainer';
import { Link } from 'react-router'
import PropTypes from 'prop-types'

const GovUkPhaseBanner = ({basicVersion}) => {
    if (basicVersion) {
        return (
            <div className="phase-banner mobile-pad no-cell key-content"><p><strong className="phase-tag">ALPHA</strong><span>This is a new service – your feedback will help us to improve it.&nbsp;</span><span/></p></div>
        )
    }
    return (
        <div className="phase-banner phase-banner-boxed mobile-pad no-cell key-content"><p><strong className="phase-tag">ALPHA</strong><span>This is a new service – your <Link tabIndex="1" to='feedback'>feedback</Link> will help us to improve it.&nbsp;</span>Access the <LegacySearchLink tabIndex="1">previous search</LegacySearchLink> here.<span/></p></div>
    )
};



GovUkPhaseBanner.propTypes = {
    basicVersion: PropTypes.bool
};


export default GovUkPhaseBanner;