package terraform.security

deny[msg] {
  input.resource_type == "digitalocean_droplet"
  not input.values.vpc_uuid
  msg := sprintf("Droplet %q must be in a VPC", [input.values.name])
}
