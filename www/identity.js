var identity = {
    read: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'Identity', 'readIdentity', []);
    }
    ,
    init: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'Identity', 'initIdentityDev', tags);
    }
    ,
    release: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'Identity', 'releaseIdentityDev', []);
    }
};
module.exports = identity;