import {Button, Icon} from "./utils.js";


export default function UserLinkIcon(userId){

    return Button('icon-button',
        () => {
            location.href = `#users/${userId}`
        },
        Icon('bx', 'bx-user')
    )

}