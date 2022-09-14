import admin from "./modules/admin";
import group from "./modules/group";
import system from "./modules/system";



export default {
    ...admin,
    ...group,
    ...system
}