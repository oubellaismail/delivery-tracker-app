package terraform.security

deny[msg] {
  input.resource_type == "digitalocean_firewall"
  some i
  rule := input.values.inbound_rule[i]
  rule.protocol == "tcp"
  rule.port_range == "22"
  rule.source_addresses[_] == "0.0.0.0/0"
  msg := "SSH from 0.0.0.0/0 is forbidden"
}
